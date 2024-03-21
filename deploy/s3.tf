# -- S3 Bucket --
resource "aws_s3_bucket" "main" {
  bucket = "${var.name_space}bucket"

  tags = {
    Name = "${var.name_space}_S3Bucket_${var.environment}"
  }
}

resource "aws_s3_bucket_ownership_controls" "main" {
  bucket = aws_s3_bucket.main.id

  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

resource "aws_s3_bucket_public_access_block" "main" {
  bucket                  = aws_s3_bucket.main.id
  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_acl" "main" {
  bucket     = aws_s3_bucket.main.id
  depends_on = [
    aws_s3_bucket_ownership_controls.main,
    aws_s3_bucket_public_access_block.main
  ]

  acl = "public-read"
}

### Allow SDK user to access bucket
#data "aws_iam_policy_document" "s3" {
#  statement {
#    actions = [
#      "s3:GetObject",
#      "s3:PutObject",
#      "s3:DeleteObject",
#      "s3:AbortMultipartUpload",
#      "s3:ListBucketMultipartUploads",
#      "s3:ListMultipartUploadParts",
#      "s3:PutAccelerateConfiguration"
#    ]
#
#    effect = "Allow"
#
#    principals {
#      identifiers = [var.trusted_user_arn]
#      type        = "AWS"
#    }
#  }
#}

output "s3_url" {
  value = "s3://${aws_s3_bucket.main.bucket}"
}