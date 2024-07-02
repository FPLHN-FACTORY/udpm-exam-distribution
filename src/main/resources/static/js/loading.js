const showLoading = () => {
    const loadingElement = `
        <div id="loading" class="loading-overlay" th:style="'display:none;'">
        <div class="loading-spinner"></div>
    </div>
    `;
    document.body.insertAdjacentHTML('beforeend', loadingElement);
};

const hideLoading = () => {
    const loadingElement = $('#loading');
    loadingElement.remove();
}