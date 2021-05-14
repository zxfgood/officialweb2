function callbackFn(details) {
    return {
        authCredentials: {
            username: "HL7F5JF125K85K8D",
            password: "FC393F432489B2E5"
        }
    };
}
 
chrome.webRequest.onAuthRequired.addListener(
        callbackFn,
        {urls: ["<all_urls>"]},
        ['blocking']
);