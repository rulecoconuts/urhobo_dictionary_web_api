# -- ROUTE 53 --
data "aws_route53_zone" "top" {
  name = var.domain_name
}

resource "aws_route53_zone" "service" {
  name = "${var.environment}.${var.domain_name}"

  tags = {
    Environment = var.environment
  }
}

# -- Link subdomain hosted zone to top hosted zone
resource "aws_route53_record" "top_link" {
  name    = "${var.environment}"
  type    = "NS"
  zone_id = data.aws_route53_zone.top.zone_id
  ttl     = 86400

  records = aws_route53_zone.service.name_servers
}

#resource "aws_route53_record" "service" {
#  zone_id = aws_route53_zone.service.zone_id
#  name    = aws_route53_zone.service.name
#  type    = "NS"
#  ttl     = 300
#  records = aws_route53_zone.service.name_servers
#}

resource "aws_route53_record" "alb_record" {
  name    = aws_route53_zone.service.name
  type    = "A"
  zone_id = aws_route53_zone.service.zone_id

  alias {
    evaluate_target_health = false
    name                   = aws_alb.alb.dns_name
    zone_id                = aws_alb.alb.zone_id
  }
}

# -- SSL CERTIFICATE --

# SSL Certificate for Application Load Balancer
resource "aws_acm_certificate" "alb_certificate" {
  domain_name               = data.aws_route53_zone.top.name
  validation_method         = "DNS"
  subject_alternative_names = ["*.${data.aws_route53_zone.top.name}"]
}

resource "aws_acm_certificate_validation" "alb_certificate" {
  certificate_arn         = aws_acm_certificate.alb_certificate.arn
  validation_record_fqdns = [aws_route53_record.generic_certificate_validation.fqdn]
}

resource "aws_route53_record" "generic_certificate_validation" {
  name    = tolist(aws_acm_certificate.alb_certificate.domain_validation_options)[0].resource_record_name
  type    = tolist(aws_acm_certificate.alb_certificate.domain_validation_options)[0].resource_record_type
  zone_id = data.aws_route53_zone.top.zone_id
  records = [tolist(aws_acm_certificate.alb_certificate.domain_validation_options)[0].resource_record_value]
  ttl     = 300
}