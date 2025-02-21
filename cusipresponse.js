const { v4: uuidv4 } = require('uuid');

const generateResponse = (requestBody) => {
    console.log("Received requestBody:", requestBody);

    // Ensure requestBody is an array, otherwise return an error response
    if (!Array.isArray(requestBody)) {
        return {
            error: "Invalid input format. Expected an array of CUSIP IDs."
        };
    }

    // Iterate over the array and generate a response for each CUSIP ID
    const responses = requestBody.map(cusipId => ({
        message: "This is the dynamic response!",
        timestamp: new Date().toISOString(),
        additionalInfo: "Hari, feel free to add more details here.",
        cusipId: cusipId,  // Use the current CUSIP ID from the array
        messageId: uuidv4(),
        originMessageTimestamp: new Date().toISOString(),
        s3FilePath: "aws-sfn21-s3-us-east-1-bi-eksirsa-sfss-fe7332-fsp",
        s3FileChecksum: "70ee1738b6b21e2c8a43f3a5ab0eee71"
    }));

    return responses;
};

module.exports = generateResponse;
