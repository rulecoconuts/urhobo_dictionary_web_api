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
  default = true
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
  default = 550
}

variable "memory" {
  type    = number
  default = 550
}

variable "log_retention_in_days" {
  type    = number
  default = 14
}

variable "ecs_desired_count" {
  type    = number
  default = 1
}

variable "ecs_min_autoscaling_count" {
  type    = number
  default = 1
}

variable "ecs_max_autoscaling_count" {
  type    = number
  default = 2
}

variable "ecs_min_task_count" {
  type    = number
  default = 1
}

variable "ecs_max_task_count" {
  type    = number
  default = 2
}

variable "cpu_target_tracking_desired_value" {
  type    = number
  default = 500
}

variable "memory_target_tracking_desired_value" {
  type    = number
  default = 500
}

variable "max_scaling_step_size" {
  type    = number
  default = 1
}

variable "min_scaling_step_size" {
  type    = number
  default = 1
}

variable "target_capacity" {
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

variable "env_file_s3_bucket" {
  default = "personalappenvfiles"
}

variable "env_file_s3_bucket_path" {
  default = "urhobo_dictionary_server.env"
}

variable "db_max_storage" {
  type    = number
  default = 10
}

variable "health_check_grace_period" {
  type    = number
  default = 1800
}

variable "elb_healthy_threshold" {
  type    = number
  default = 2
}

variable "elb_unhealthy_threshold" {
  default = 10
}

variable "elb_health_check_interval" {
  description = "Interval between health-checks"
  type        = number
  default     = 180
}

variable "elb_health_check_timeout" {
  description = "Duration for the Elastic load balancer to wait for a task to be ready"
  type        = number
  default     = 120
}

variable "trusted_user_arn" {
  default = "arn:aws:iam::992382640465:user/langresus_api"
}