# 开发阶段
FROM  swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/node:22.15.0

WORKDIR /app
COPY package*.json ./

RUN npm install

COPY . .

EXPOSE 5173

CMD ["npm", "run", "dev"]
