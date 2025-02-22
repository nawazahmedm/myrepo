module.exports = function (request) {
    let input = request.body;

    if (!input || !input.transactionId || !input.securities || !Array.isArray(input.securities)) {
        return { error: "Invalid input format" };
    }

    let response = {
        transactionId: input.transactionId,
        securities: input.securities.map(security => ({
            identifier: {
                cusipNumber: security.cusipNumber
            },
            securityExecutionType: "ResecuritizationSingleClass",
            securityExecutionSubType: "Giant",
            securityPrefixCode: "GO",
            securityInterestVariabilityType: "Fixed",
            securityDescription: "30 Year Fixed Rate Giant",
            securityExecutionTypePathFinders: "ResecuritizationSingleClass",
            securityInterestVariabilityTypePathFinders: "Fixed"
        }))
    };

    return response;
};
