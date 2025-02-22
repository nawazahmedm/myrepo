function generateWireTransactionResponse(inputData, n = 3) {
  const { transactionId, requestingSystem, wireGroupTransactionID } = inputData;
  const securityWires = [];

  for (let i = 1; i <= n; i++) {
    securityWires.push({
      securityWirePoolIdentifier: `RP8${i}10`,
      securityWireCUSIPIdentifier: `${i}142G9YG9`,
      seriesNumber: "000415",
      contractIdentifier: null,
      parentCUSIPIdentifier: null,
      wireGroupTotalProceedsAmount: 0,
      wireGroupTotalSecurityFaceAmount: 1738818,
      wires: [
        {
          securityWireFaceValueAmount: 1738818,
          securityWireProceedsAmount: 0,
          wireTransferIdentifier: `${i}142G9YG90011`,
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
    });
  }

  return {
    transactionId: transactionId || "default-transaction-id",
    requestingSystem: requestingSystem || "default-requesting-system",
    wireGroupTransactionID: wireGroupTransactionID || "default-wire-group-transaction-id",
    securityWires: securityWires,
  };
}

module.exports = { generateWireTransactionResponse };
