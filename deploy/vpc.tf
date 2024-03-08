resource "aws_vpc" "main" {
  cidr_block           = "${var.vpc_ip}/${var.vpc_ip_prefix_length}"
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "${var.name_space}_VPC_${var.environment}"
  }
}

resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${var.name_space}_InternetGateway_${var.environment}"
  }
}

data "aws_availability_zones" "available" {
  state = "available"
}

# -- PUBLIC SUBNETS --

## One public subnet per availability zone
resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.main.id
  count                   = var.availability_zone_count
  availability_zone       = data.aws_availability_zones.available.names[count.index]
  cidr_block              = cidrsubnet(aws_vpc.main.cidr_block, 8, var.availability_zone_count+count.index)
  map_public_ip_on_launch = true
  tags                    = {
    Name = "${var.name_space}_PublicSubnet_${count.index}_${var.environment}"
  }
}

## Route table that allows outgoing traffic to the internet
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  tags = {
    Name = "${var.name_space}_PublicSubnetRouteTable_${count.index}_${var.environment}"
  }
}

## Associate outgoing public route table to public subnets
resource "aws_route_table_association" "public" {
  count          = var.availability_zone_count
  route_table_id = aws_route_table.public.id
  subnet_id      = aws_subnet.public[count.index].id
}

## Make outgoing public route table the default for the VPC
resource "aws_route_table_association" "public_main" {
  vpc_id         = aws_vpc.main.id
  route_table_id = aws_route_table.public.id
}

# -- PRIVATE SUBNETS --

## Create one Elastic IP Address per availability zone (One nat gateway per availability zone)
resource "aws_eip" "nat" {
  count  = var.availability_zone_count
  domain = "vpc"


  tags = {
    Name = "${var.name_space}_EIP_${count.index}_${var.environment}"
  }
}

## Create one NAT Gateway per availability Zone
resource "aws_nat_gateway" "private" {
  count         = var.availability_zone_count
  subnet_id     = aws_subnet.public[count.index].id
  allocation_id = aws_eip.nat[count.index].id
  tags          = {
    Name = "${var.name_space}_NATGateway_${count.index}_${var.environment}"
  }
}

## Create one private subnet per availability zone
resource "aws_subnet" "private" {
  count             = var.availability_zone_count
  vpc_id            = aws_vpc.main.id
  cidr_block        = cidrsubnet(aws_vpc.main.cidr_block, 8, count.index)
  availability_zone = data.aws_availability_zones.available.names[count.index]

  tags = {
    Name = "${var.name_space}_PrivateSubnet_${count.index}_${var.environment}"
  }
}

## Create one route table per private subnet
resource "aws_route_table" "private" {
  count  = var.availability_zone_count
  vpc_id = aws_vpc.main.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.private[count.index].id
  }

  tags = {
    Name = "${var.name_space}_PrivateSubnetRouteTable_${count.index}_${var.environment}"
  }
}

## Associate private route tables with their corresponding private subnet
resource "aws_route_table_association" "private" {
  count          = var.availability_zone_count
  route_table_id = aws_route_table.private[count.index].id
  subnet_id      = aws_subnet.private[count.index].id
}