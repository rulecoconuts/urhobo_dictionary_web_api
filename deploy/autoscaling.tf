## Create Auto scaling group linked with main VPC
resource "aws_autoscaling_group" "ecs_autoscaling_group" {
  max_size              = var.ecs_max_autoscaling_count
  min_size              = var.ecs_min_autoscaling_count
  vpc_zone_identifier   = aws_subnet.private[*].id
  health_check_type     = "EC2"
  protect_from_scale_in = false
  #  desired_capacity      = var.ecs_desired_count

  target_group_arns = [aws_alb_target_group.service.arn]

  enabled_metrics = [
    "GroupMinSize",
    "GroupMaxSize",
    "GroupDesiredCapacity",
    "GroupInServiceInstances",
    "GroupPendingInstances",
    "GroupStandbyInstances",
    "GroupTerminatingInstances",
    "GroupTotalInstances"
  ]

  launch_template {
    id      = aws_launch_template.main.id
    version = aws_launch_template.main.latest_version
  }

  instance_refresh {
    strategy = "Rolling"
    preferences {
      min_healthy_percentage = 50
      skip_matching          = true
    }
    triggers = ["launch_template"]
  }

  lifecycle {
    create_before_destroy = true
  }

  tag {
    key                 = "Name"
    propagate_at_launch = true
    value               = "${var.name_space}_ASG_${var.environment}"
  }
}

### Define target tracking on ECS cluster task level
#resource "aws_appautoscaling_target" "ecs_target" {
#  max_capacity       = var.ecs_max_task_count
#  min_capacity       = var.ecs_min_task_count
#  resource_id        = "service/${aws_ecs_cluster.main.name}/${aws_ecs_service.service.name}"
#  scalable_dimension = "ecs:service:DesiredCount"
#  service_namespace  = "ecs"
#}

## Policy for CPU tracking on autoscaling group
#resource "aws_appautoscaling_policy" "ecs_cpu_policy" {
#  name               = "${var.name_space}_CPUTargetTrackingScaling_${var.environment}"
#  resource_id        = aws_appautoscaling_target.ecs_target.resource_id
#  scalable_dimension = aws_appautoscaling_target.ecs_target.scalable_dimension
#  service_namespace  = aws_appautoscaling_target.ecs_target.service_namespace
#  policy_type        = "TargetTrackingScaling"
#
#  target_tracking_scaling_policy_configuration {
#    target_value = var.cpu_target_tracking_desired_value
#
#    predefined_metric_specification {
#      predefined_metric_type = "ECSServiceAverageCPUUtilization"
#    }
#  }
#}

#resource "aws_appautoscaling_policy" "ecs_memory_policy" {
#  name               = "${var.name_space}_MemoryTargetTrackingScaling_${var.environment}"
#  resource_id        = aws_appautoscaling_target.ecs_target.resource_id
#  scalable_dimension = aws_appautoscaling_target.ecs_target.scalable_dimension
#  service_namespace  = aws_appautoscaling_target.ecs_target.service_namespace
#  policy_type        = "TargetTrackingScaling"
#
#  target_tracking_scaling_policy_configuration {
#    target_value = var.memory_target_tracking_desired_value
#
#    predefined_metric_specification {
#      predefined_metric_type = "ECSServiceAverageMemoryUtilization"
#    }
#  }
#}

## Create ECS Capacity provider
resource "aws_ecs_capacity_provider" "service" {
  name = "${var.name_space}_ECS_CapacityProvider_${var.environment}"

  auto_scaling_group_provider {
    auto_scaling_group_arn         = aws_autoscaling_group.ecs_autoscaling_group.arn
    managed_termination_protection = "DISABLED"


    managed_scaling {
      maximum_scaling_step_size = var.max_scaling_step_size
      minimum_scaling_step_size = var.min_scaling_step_size
      status                    = "ENABLED"
      target_capacity           = var.target_capacity
    }
  }
}

resource "aws_ecs_cluster_capacity_providers" "service" {
  cluster_name = aws_ecs_cluster.main.name

  capacity_providers = [aws_ecs_capacity_provider.service.name]
}