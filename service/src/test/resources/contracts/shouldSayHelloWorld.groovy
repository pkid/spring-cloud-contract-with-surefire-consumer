org.springframework.cloud.contract.spec.Contract.make {
    request { // (1)
        method 'GET' // (2)
        url '/greeting' // (3)
        //body([ // (4)
        //       "client.id": $(regex('[0-9]{10}')),
        //       loanAmount: 99999
        //])
        headers { // (5)
            contentType('application/json')
        }
    }
    response { // (6)
        status 200 // (7)
        body([ // (8)
               "content": "Hello, World!"
        ])
        headers { // (9)
            contentType('application/json;charset=UTF-8')
        }
    }
}
