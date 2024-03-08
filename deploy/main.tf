terraform {
  backend "remote" {
    organization = "coconut_projects"
    workspaces {
      name = "urhobo_dictionary_workspace"
    }
  }
  required_providers {
    aws = {
      source  = "hasicorp/aws"
      version = "~> 5.39"
    }
  }
}

variable "environment" {
  default = "dev"
}

variable "name_space" {
  default = "langresus"
}

variable "service_name" {
  default = "langresus-api"
}

variable "domain_name" {
  default = "langresus.com"
}

variable "ecr_force_delete" {
  type    = bool
  default = false
}

variable "vpc_ip" {
  default = "10.34.0.0"
}

variable "vpc_ip_prefix_length" {
  type    = number
  default = 16
}

variable "availability_zone_count" {
  type    = number
  default = 2
}

variable "public_ec2_key" {
  default = ""
}

variable "ec2_instance_type" {
  default = "t2.micro"
}

variable "health_check_matcher" {
  default = "200"
}

variable "health_check_endpoint" {
  default = "/health-check"
}