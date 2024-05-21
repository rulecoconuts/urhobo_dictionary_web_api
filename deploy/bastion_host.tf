#resource "aws_security_group" "bastion" {
#  name = "${var.name_space}_Bastion_SecurityGroup_${var.environment}"
#
#  vpc_id = aws_vpc.main.id
#
#  ingress {
#    description = "Allow all incoming SSH traffic"
#    from_port   = 22
#    to_port     = 22
#    protocol    = "tcp"
#    cidr_blocks = ["0.0.0.0/0"]
#  }
#
#  egress {
#    description = "Allow all outgoing traffic"
#    from_port   = 0
#    to_port     = 0
#    protocol    = "-1"
#    cidr_blocks = ["0.0.0.0/0"]
#  }
#}
#
#data "aws_ami" "amazon_linux_2" {
#  most_recent = true
#
#  filter {
#    name   = "virtualization-type"
#    values = ["hvm"]
#  }
#
#  filter {
#    name   = "owner-alias"
#    values = ["amazon"]
#  }
#
#  filter {
#    name   = "name"
#    values = ["amzn2-ami-ecs-hvm-*-x86_64-ebs"]
#  }
#
#  owners = ["amazon"]
#}
#
#resource "aws_instance" "bastion" {
#  ami                         = data.aws_ami.amazon_linux_2.image_id
#  instance_type               = "t2.micro"
#  subnet_id                   = aws_subnet.private[0].id
#  associate_public_ip_address = true
#  key_name                    = aws_key_pair.default.key_name
#  vpc_security_group_ids      = [aws_security_group.bastion.id]
#
#  tags = {
#    Name = "${var.name_space}_BastionHost_EC2_${var.environment}"
#  }
#}
#
