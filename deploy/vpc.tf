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

# -- PRIVATE SUBNETS --

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
  vpc_id = aws_vpc.main.id

  route {
    ## All internet traffic will be routed through internet gateway
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  tags = {
    Name = "${var.name_space}_PrivateSubnetRouteTable_${var.environment}"
  }
}

## Associate private route tables with their corresponding private subnet
resource "aws_route_table_association" "private" {
  count          = var.availability_zone_count
  route_table_id = aws_route_table.private.id
  subnet_id      = aws_subnet.private[count.index].id
}

## Make outgoing private route table the default for the VPC
resource "aws_main_route_table_association" "private_main" {
  vpc_id         = aws_vpc.main.id
  route_table_id = aws_route_table.private.id
}

/*
The following endpoints are necessary for private VPC
com.amazonaws.region.ecs-agent
com.amazonaws.region.ecs-telemetry
com.amazonaws.region.ecs
*/
resource "aws_vpc_endpoint" "ecs_agent" {
  service_name      = "com.amazonaws.${var.region}.ecs-agent"
  vpc_id            = aws_vpc.main.id
  vpc_endpoint_type = "Interface"
  subnet_ids        = aws_subnet.private.*.id
}

resource "aws_vpc_endpoint" "ecs_telemetry" {
  service_name      = "com.amazonaws.${var.region}.ecs-telemetry"
  vpc_id            = aws_vpc.main.id
  vpc_endpoint_type = "Interface"
  subnet_ids        = aws_subnet.private.*.id
}

resource "aws_vpc_endpoint" "ecs" {
  service_name      = "com.amazonaws.${var.region}.ecs"
  vpc_id            = aws_vpc.main.id
  vpc_endpoint_type = "Interface"
  subnet_ids        = aws_subnet.private.*.id
}

resource "aws_vpc_endpoint" "ecr_dkr" {
  service_name      = "com.amazonaws.${var.region}.ecr.dkr"
  vpc_id            = aws_vpc.main.id
  vpc_endpoint_type = "Interface"
  subnet_ids        = aws_subnet.private.*.id
}

resource "aws_vpc_endpoint" "ecr_api" {
  service_name      = "com.amazonaws.${var.region}.ecr.api"
  vpc_id            = aws_vpc.main.id
  vpc_endpoint_type = "Interface"
  subnet_ids        = aws_subnet.private.*.id
}

resource "aws_vpc_endpoint" "s3_gateway" {
  service_name      = "com.amazonaws.${var.region}.s3"
  vpc_id            = aws_vpc.main.id
  vpc_endpoint_type = "Gateway"
}