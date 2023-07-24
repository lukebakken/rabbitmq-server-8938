# rabbitmq-server-8938

## Discussion

https://github.com/rabbitmq/rabbitmq-server/discussions/8938

## Enable debug logging

```
rabbitmq:
    image: rabbitmq:3.9-management
    hostname: rabbitmq
    environment:
        - LOG=debug
    volumes:
        - ./rabbitmq.conf:/etc/rabbitmq/rabbitmq.conf
        - ./rabbitmq-env.conf:/etc/rabbitmq/rabbitmq-env.conf
```
