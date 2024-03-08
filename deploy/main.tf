terraform {
  backend "remote" {
    organization = "coconut_projects"
    workspaces {
      name = "urhobo_dictionary_workspace"
    }
  }
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.39"
    }
  }
}

variable "region" {
  default = "ca-central-1"
}

provider "aws" {
  region = var.region
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

variable "hash" {
  default = "latest"
}

variable "cpu_units" {
  type    = number
  default = 900
}

variable "memory" {
  type    = number
  default = 900
}

variable "log_retention_in_days" {
  type    = number
  default = 14
}

variable "ecs_desired_count" {
  type    = number
  default = 1
}

variable "ecs_minimum_healthy_percentage" {
  type    = number
  default = 100
}

variable "ecs_maximum_healthy_percentage" {
  type    = number
  default = 200
}

variable "ecs_container_port" {
  type    = number
  default = 8080
}