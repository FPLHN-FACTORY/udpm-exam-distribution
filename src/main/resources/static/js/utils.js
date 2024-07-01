function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

const formatFromUnixTimeToDate = (unixTime) => {
    return new Date(unixTime).toLocaleDateString();
}

const getValueForInputDate = (unix) => {
    return new Date(unix).toLocaleDateString('en-CA');
}

function debounce(func, timeout = 300) {
    let timer;
    return (...args) => {
        clearTimeout(timer);
        timer = setTimeout(() => {
            func.apply(this, args);
        }, timeout);
    };
}

const getUrlParameters = (apiURL, params) => {
    let url = apiURL + '?';
    for (let [key, value] of Object.entries(params)) {
        if (value) {
            url += `${key}=${value}&`;
        }
    }
    url = url.slice(0, -1);
    return url;
}