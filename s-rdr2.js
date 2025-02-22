const { v4: uuidv4 } = require('uuid');

const generateResponse = (requestBody) => {
    console.log("Received requestBody:", requestBody);

    // Validate input
    if (!requestBody || !requestBody.transactionId || !Array.isArray(requestBody.securities)) {
        return {
            error: "Invalid input format. Expected a transactionId and an array of securities."
        };
    }

    // Iterate over securities and generate response dynamically
    const securitiesResponse = requestBody.securities.map(security => {
        let identifier = {};

        // Assign cusipNumber or poolNumber if available
        if (security.cusipNumber) {
            identifier.cusipNumber = security.cusipNumber;
        }
        if (security.poolNumber) {
            identifier.poolNumber = security.poolNumber;
        }

        return {
            identifier: identifier,
            securityExecutionType: "ResecuritizationSingleClass",
            securityExecutionSubType: "Giant",
            securityPrefixCode: "GO",
            securityInterestVariabilityType: "Fixed",
            securityDescription: "30 Year Fixed Rate Giant",
            securityExecutionTypePathFinders: "ResecuritizationSingleClass",
            securityInterestVariabilityTypePathFinders: "Fixed",
            messageId: uuidv4(),
            timestamp: new Date().toISOString()
        };
    });

    return {
        transactionId: requestBody.transactionId,
        securities: securitiesResponse
    };
};

module.exports = generateResponse;
