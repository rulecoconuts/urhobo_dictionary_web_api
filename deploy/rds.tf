# -- AWS DB Instance --
variable "db_username" {
  default = "urhobo-dictionary"
}

variable "db_password" {
  default = ""
}

resource "aws_security_group" "db_main" {
  vpc_id = aws_vpc.main.id

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = aws_subnet.private[*].cidr_block
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_db_subnet_group" "main" {
  subnet_ids = aws_subnet.private[*].id
  name       = "${var.name_space}_rds_${var.environment}"
}

resource "aws_db_instance" "main" {
  instance_class          = "db.t3.micro"
  allocated_storage       = 10
  db_name                 = "${replace(replace(var.service_name, "-", ""), "_", "")}db"
  engine                  = "postgres"
  username                = var.db_username
  password                = var.db_password
  identifier              = "${var.service_name}-db"
  backup_retention_period = 7
  vpc_security_group_ids  = [aws_security_group.db_main.id]
  multi_az                = false // Single-AZ to qualify for free tier
  db_subnet_group_name    = aws_db_subnet_group.main.name
  #  final_snapshot_identifier = "urhobo-dictionary-db-final-snapshot"
  skip_final_snapshot     = true
}

output "db_url" {
  value = aws_db_instance.main.endpoint
}

