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

resource "aws_ecs_service" "service" {
  name     = "${var.name_space}_ECSService_${var.environment}"
  iam_role = aws_iam_role.ecs_service.arn
}