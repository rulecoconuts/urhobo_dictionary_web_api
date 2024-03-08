resource "aws_security_group" "alb" {
  name   = "${var.name_space}_ALB_SecurityGroup_${var.environment}"
  vpc_id = aws_vpc.main.id

  egress {
    description = "Allow all outgoing traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.name_space}_ALB_SecurityGroup_${var.environment}"
  }
}

## Application Load Balancer in public subnets
resource "aws_alb" "alb" {
  name            = "${var.name_space}-ALB-${var.environment}"
  security_groups = [aws_security_group.alb.id]
  subnets         = aws_subnet.public[*].id
}

resource "aws_alb_target_group" "service" {
  name                 = "${var.name_space}_ALB_TargetGroup_${var.environment}"
  port                 = 80
  protocol             = "HTTP"
  vpc_id               = aws_vpc.main.id
  deregistration_delay = "120"

  health_check {
    healthy_threshold   = 2
    unhealthy_threshold = 2
    interval            = 125
    matcher             = var.health_check_matcher
    path                = var.health_check_endpoint
    port                = "traffic-port"
    protocol            = "HTTP"
    timeout             = 120
  }

  depends_on = [aws_alb.alb]

}

## Default HTTPS listener
resource "aws_alb_listener" "alb_default_listener_https" {
  load_balancer_arn = aws_alb.alb.arn
  port              = 443
  protocol          = "HTTPS"
  certificate_arn   = aws_acm_certificate.alb_certificate.arn
  ssl_policy        = "ELBSecurityPolicy-TLS-1-2-Ext-2018-06"

  depends_on = [aws_acm_certificate.alb_certificate]
}

resource "aws_alb_listener_rule" "https_listener_rule" {
  listener_arn = aws_alb_listener.alb_default_listener_https.arn

  action {
    type             = "forward"
    target_group_arn = aws_alb_target_group.service.arn
  }
}