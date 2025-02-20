const uuid = require('uuid');
const moment = require('moment');
const fs = require('fs');

function generateCUSIP() {
    return Math.random().toString(36).substring(2, 12).toUpperCase();
}

function generateNumericId(length) {
    return Math.floor(Math.random() * Math.pow(10, length)).toString();
}

function generateRoutingNumber() {
    return Math.floor(100000000 + Math.random() * 900000000).toString();
}

function logic(request, response, logger) {
    try {
        let requestBody = JSON.parse(request.body);

        let transactionId = requestBody.transactionId || uuid.v4();
        let requestingSystem = requestBody.requestingSystem || "UnknownSystem";

        let template = fs.readFileSync("responseTemplate.json", "utf-8");
        let responseTemplate = JSON.parse(template);

        responseTemplate.transactionId = transactionId;
        responseTemplate.requestingSystem = requestingSystem;

        // Generate multiple security wires dynamically
        responseTemplate.securityWires = [...Array(Math.floor(Math.random() * 5) + 1)].map(() => ({
            securityWirePoolIdentifier: `RPS${generateNumericId(4)}`,
            securityWireCUSIPIdentifier: generateCUSIP(),
            seriesNumber: generateNumericId(6),
            contractIdentifier: uuid.v4(),
            parentCUSIPIdentifier: generateCUSIP(),
            wireGroupTotalProceedsAmount: Math.random() * 1000000,
            wireGroupTotalSecurityFaceAmount: Math.random() * 5000000,
            wires: [...Array(Math.floor(Math.random() * 3) + 1)].map(() => ({
                securityWireProceedsAmount: Math.random() * 500000,
                wireTransferIdentifier: uuid.v4(),
                wireInstructionExecutionDate: moment().format("YYYY-MM-DD"),
                wireTransferDirectionType: "Deliver",
                wireReceiverAdviceInformation: "Advice Info Text",
                securityClassCode: "FPC",
                sender: {
                    wirePartyRoleType: "Sender FinancialInstitution",
                    abaRoutingAndTransitIdentifier: generateRoutingNumber(),
                    financialInstitutionAccountIdentifier: generateNumericId(6),
                    financialInstitutionAccountSubaccountName: generateNumericId(4),
                },
                receiver: {
                    wirePartyRoleType: "Receiver FinancialInstitution",
                    abaRoutingAndTransitIdentifier: generateRoutingNumber(),
                    financialInstitutionAccountIdentifier: generateNumericId(8),
                    financialInstitutionAccountSubaccountName: generateNumericId(5),
                }
            }))
        }));

        response.body = responseTemplate;
    } catch (error) {
        logger.error("Error processing response: " + error.message);
        response.body = { error: "Failed to process request" };
    }
}
