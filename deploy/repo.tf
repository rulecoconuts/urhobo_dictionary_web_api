resource "aws_ecr_repository" "ecr" {
  name         = "${var.name_space}/${var.service_name}"
  force_delete = var.ecr_force_delete
  image_scanning_configuration {
    scan_on_push = false
  }
}

output "repo_url" {
  value = aws_ecr_repository.ecr.repository_url
}