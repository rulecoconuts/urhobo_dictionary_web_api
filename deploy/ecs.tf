resource "aws_ecs_cluster" "main" {
  name = "${var.name_space}_ECSCluster_${var.environment}"

  lifecycle { create_before_destroy = true }

  tags = {
    Name = "${var.name_space}_ECSCluster_${var.environment}"
  }
}

# -- ECS SERVICE IAM --

## Allow ECS Service assume roles
data "aws_iam_policy_document" "ecs_service" {
  statement {
    effect = "Allow"

    principals {
      identifiers = ["ecs.amazonaws.com"]
      type        = "Service"
    }
    actions = ["sts:AssumeRole"]
  }
}

resource "aws_iam_role" "ecs_service" {
  assume_role_policy = data.aws_iam_policy_document.ecs_service.json
  name               = "${var.name_space}_ECSServiceRole_${var.environment}"
}

data "aws_iam_policy_document" "ecs_service_role_policy" {
  statement {
    effect    = "Allow"
    resources = ["*"]
    actions   = [
      "ec2:AuthorizeSecurityGroupIngress",
      "ec2:Describe*",
      "elasticloadbalancing:DeregisterInstancesFromLoadBalancer",
      "elasticloadbalancing:DeregisterTargets",
      "elasticloadbalancing:Describe*",
      "elasticloadbalancing:RegisterInstancesWithLoadBalancer",
      "elasticloadbalancing:RegisterTargets",
      "ec2:DescribeTags",
      "logs:CreateLogGroup",
      "logs:CreateLogStream",
      "logs:DescribeLogStreams",
      "logs:PutSubscriptionFilter",
      "logs:PutLogEvents"
    ]
  }
}

resource "aws_iam_role_policy" "ecs_service_role_policy" {
  name   = "${var.name_space}_ECSService_Role_${var.environment}"
  policy = data.aws_iam_policy_document.ecs_service_role_policy.json
  role   = aws_iam_role.ecs_service.id
}

data "aws_iam_policy_document" "task_assume_role_policy" {
  statement {
    effect  = "Allow"
    actions = ["sts:AssumeRole"]
    principals {
      identifiers = ["ecs-tasks.amazonaws.com"]
      type        = "Service"
    }
  }
}

## Execution and task IAM
resource "aws_iam_role" "ecs_task_execution_role" {
  assume_role_policy = data.aws_iam_policy_document.task_assume_role_policy.json
  name               = "${var.name_space}_ECS_TaskExecutionRole_${var.environment}"

  inline_policy {
    name = "log-stream-creation-policy"

    policy = jsonencode({
      Version   = "2012-10-17",
      Statement = {
        Action = [
          "logs:CreateLogStream",
          "logs:PutLogEvents",
          "logs:DescribeLogStreams"
        ]
        Effect = "Allow"

        Resource = "arn:aws:logs:*:*:*"
      }
    })
  }

  inline_policy {
    name = "allow-read-environment-file"

    policy = jsonencode({
      Version   = "2012-10-17",
      Statement = [
        {
          Action = [
            "s3:GetObject"
          ]

          Effect = "Allow"

          Resource = "arn:aws:s3:::${var.env_file_s3_bucket}/${var.env_file_s3_bucket_path}"
        },
        {
          Effect = "Allow"
          Action = [
            "s3:GetBucketLocation"
          ]
          Resource = [
            "arn:aws:s3:::${var.env_file_s3_bucket}/",
            "arn:aws:s3:::${var.env_file_s3_bucket}",
            "arn:aws:s3:::${var.env_file_s3_bucket}/*"
          ]
        }
      ]
    })
  }
}

## Attach AWS Managed policy for ECS task execution role
resource "aws_iam_role_policy_attachment" "task_assume_role_policy" {
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
  role       = aws_iam_role.ecs_task_execution_role.name
}

resource "aws_iam_role" "ecs_task_iam_role" {
  assume_role_policy = data.aws_iam_policy_document.task_assume_role_policy.json
  name               = "${var.name_space}_ECS_TaskRole_${var.environment}"
}

# -- MAIN --
resource "aws_cloudwatch_log_group" "log_group" {
  name              = "/${var.name_space}/ecs/${var.service_name}"
  retention_in_days = var.log_retention_in_days
}

resource "aws_ecs_task_definition" "service" {
  family = "${var.name_space}_ECS_TaskDefinition_${var.environment}"

  task_role_arn      = aws_iam_role.ecs_task_iam_role.arn
  execution_role_arn = aws_iam_role.ecs_task_execution_role.arn

  container_definitions = jsonencode([
    {
      name         = var.service_name
      image        = "${aws_ecr_repository.ecr.repository_url}:${var.hash}"
      cpu          = var.cpu_units
      memory       = var.memory
      essential    = true
      portMappings = [
        {
          containerPort = var.ecs_container_port
          hostPort      = 80
          protocol      = "tcp"
        }
      ]

      environmentFiles = [
        {
          value = "arn:aws:s3:::${var.env_file_s3_bucket}/${var.env_file_s3_bucket_path}"
          type  = "s3"
        }
      ]

      logConfiguration = {
        logDriver = "awslogs",
        options   = {
          "awslogs-group"         = aws_cloudwatch_log_group.log_group.name,
          "awslogs-region"        = var.region,
          "awslogs-stream-prefix" = "app"
        }
      }
    }
  ])

}

resource "aws_ecs_service" "service" {
  name                               = "${var.name_space}_ECSService_${var.environment}"
  iam_role                           = aws_iam_role.ecs_service.arn
  cluster                            = aws_ecs_cluster.main.id
  task_definition                    = aws_ecs_task_definition.service.arn
  desired_count                      = var.ecs_desired_count
  deployment_minimum_healthy_percent = var.ecs_minimum_healthy_percentage
  deployment_maximum_percent         = var.ecs_maximum_healthy_percentage

  load_balancer {
    container_name   = var.service_name
    container_port   = var.ecs_container_port
    target_group_arn = aws_alb_target_group.service.arn
  }

  ## Spread tasks evenly across all availability zones for high availability
  ordered_placement_strategy {
    type  = "spread"
    field = "attribute:ecs.availability-zone"
  }

  ## Use all the available space on the container
  ordered_placement_strategy {
    type  = "binpack"
    field = "memory"
  }

  ## Do not reset when desired count changes
  lifecycle {
    ignore_changes = [desired_count]
  }
}