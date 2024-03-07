sudo docker build -f ./Dockerfile -t afejith/urhobo_dictionary_server:latest ../

# Deploy remotely
terraform apply

# Get created repository URL
export REPO=$(terraform output --raw urhobo_dictionary_repo_url)

aws ecr get-login-password --profile afejith_admin_proxy | sudo docker login --username AWS --password-stdin $REPO

# Pull docker image and push to ECR
#sudo docker pull --platform linux/amd64 afejith/urhobo_dictionary_server:latest
sudo docker tag afejith/urhobo_dictionary_server:latest $REPO:latest
sudo docker push $REPO:latest

