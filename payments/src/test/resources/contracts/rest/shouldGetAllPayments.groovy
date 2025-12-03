import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Returns all payments")
    request {
        method 'GET'
        url '/payments'
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body([
            [
                id: $(anyPositiveInt()),
                userId: $(anyPositiveInt()),
                amount: 100.0,
                description: $(anyNonBlankString()),
                paymentMethod: $(anyNonBlankString()),
                timestamp: $(regex(/[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\.[0-9]+)?/)),
                status: "COMPLETED"
            ]
        ])
    }
}
