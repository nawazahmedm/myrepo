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

const wireTransactionResponse = {
  transactionId: "0a9345e5-239f-4270-a809-5530f9ee3d6b",
  requestingSystem: "FSPExternalIntegrator",
  wireGroupTransactionID: "3fca3102-d2c7-4e6d-8642-7d7fa4608834",
  securityWires: securityWires,
};

module.exports = wireTransactionResponse;
