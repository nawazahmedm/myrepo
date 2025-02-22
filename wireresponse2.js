const generateWireTransactionResponse = (requestBody) => {
  const securityWires = Array.from({ length: 3 }, (_, i) => ({
    securityWirePoolIdentifier: `RP8${i + 1}10`,
    securityWireCUSIPIdentifier: `${i + 1}142G9YG9`,
    seriesNumber: "000415",
    contractIdentifier: null,
    parentCUSIPIdentifier: null,
    wireGroupTotalProceedsAmount: 0,
    wireGroupTotalSecurityFaceAmount: 1738818,
    wires: [
      {
        securityWireFaceValueAmount: 1738818,
        securityWireProceedsAmount: 0,
        wireTransferIdentifier: `${i + 1}142G9YG90011`,
        wireInstructionExecutionDate: "2024-11-04",
        wireTransferDirectionType: "Deliver",
        wireReceiverAdviceInformation: "Advice Info Text",
        securityClassCode: "FEPC",
        sender: {
          wirePartyRoleType: "SenderFinancialInstitution",
          abaRoutingAndTransitIdentifier: "021033205",
          financialInstitutionAccountIdentifier: "7025",
          financialInstitutionAccountSubaccountName: "7025",
        },
        receiver: {
          wirePartyRoleType: "ReceiverFinancialInstitution",
          abaRoutingAndTransitIdentifier: "BGGA",
          financialInstitutionAccountIdentifier: "11000028",
          financialInstitutionAccountSubaccountName: "SPEC",
        },
      },
    ],
  }));

  return {
    transactionId: requestBody.transactionId,
    requestingSystem: requestBody.requestingSystem,
    wireGroupTransactionID: requestBody.wireGroupTransactionID,
    securityWires: securityWires,
  };
};

module.exports = generateWireTransactionResponse;
