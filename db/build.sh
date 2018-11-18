NAME="ris58h/goleador-db"
TAG=$(git log -1 --pretty=%h)
docker build -t ${NAME}:${TAG} .
docker tag ${NAME}:${TAG} ${NAME}:latest
