docker build -f ./Dockerfile -t afejith/urhobo_dictionary_server:latest ../

# Get created repository URL
export REPO=$(terraform output --raw repo_url)

aws ecr get-login-password --profile afejith_admin_proxy | sudo docker login --username AWS --password-stdin $REPO

# Pull docker image and push to ECR
#sudo docker pull --platform linux/amd64 afejith/urhobo_dictionary_server:latest
docker tag afejith/urhobo_dictionary_server:latest $REPO:latest
docker push $REPO:latest

# Replace ECS task definition for server
terraform taint aws_ecs_task_definition.service

# Deploy remotely
terraform apply