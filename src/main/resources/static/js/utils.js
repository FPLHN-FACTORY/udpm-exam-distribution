function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

const formatFromUnixTimeToDate = (unixTime) => {
    return new Date(unixTime).toLocaleDateString();
}

const getValueForInputDate = (unix) => {
    return new Date(unix).toLocaleDateString('en-CA');
}
