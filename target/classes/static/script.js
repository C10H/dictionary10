class DictionaryApp {
    constructor() {
        this.apiUrl = '/api/dictionary/translate';
        this.initializeElements();
        this.bindEvents();
        this.focusInput();
    }

    initializeElements() {
        this.searchInput = document.getElementById('searchInput');
        this.searchBtn = document.getElementById('searchBtn');
        this.clearBtn = document.getElementById('clearBtn');
        this.languageIndicator = document.getElementById('languageIndicator');
        this.detectedLanguage = document.getElementById('detectedLanguage');
        this.resultContainer = document.getElementById('resultContainer');
        this.resultInput = document.getElementById('resultInput');
        this.resultOutput = document.getElementById('resultOutput');
        this.resultSource = document.getElementById('resultSource');
        this.sourceBadge = document.getElementById('sourceBadge');
        this.loading = document.getElementById('loading');
        this.errorContainer = document.getElementById('errorContainer');
        this.errorMessage = document.getElementById('errorMessage');
        this.errorRetry = document.getElementById('errorRetry');
        this.exampleTags = document.querySelectorAll('.example-tag');
    }

    bindEvents() {
        this.searchBtn.addEventListener('click', () => this.handleSearch());
        this.clearBtn.addEventListener('click', () => this.clearSearch());
        this.errorRetry.addEventListener('click', () => this.handleSearch());
        
        this.searchInput.addEventListener('input', (e) => this.handleInputChange(e));
        this.searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.handleSearch();
            }
        });

        this.exampleTags.forEach(tag => {
            tag.addEventListener('click', (e) => {
                const word = e.target.dataset.word;
                this.searchInput.value = word;
                this.handleInputChange({ target: this.searchInput });
                this.handleSearch();
            });
        });
    }

    focusInput() {
        this.searchInput.focus();
    }

    handleInputChange(e) {
        const value = e.target.value.trim();
        
        if (value) {
            this.clearBtn.style.display = 'block';
            this.detectLanguage(value);
        } else {
            this.clearBtn.style.display = 'none';
            this.hideLanguageIndicator();
        }
    }

    detectLanguage(text) {
        const isChinese = this.containsChinese(text);
        const isEnglish = this.isEnglish(text);
        
        if (isChinese) {
            this.showLanguageIndicator('Chinese detected (中文)');
        } else if (isEnglish) {
            this.showLanguageIndicator('English detected');
        } else {
            this.hideLanguageIndicator();
        }
    }

    containsChinese(text) {
        const chineseRegex = /[\u4e00-\u9fa5]/;
        return chineseRegex.test(text);
    }

    isEnglish(text) {
        const englishRegex = /^[a-zA-Z\s]+$/;
        return englishRegex.test(text);
    }

    showLanguageIndicator(text) {
        this.detectedLanguage.textContent = text;
        this.languageIndicator.style.display = 'block';
    }

    hideLanguageIndicator() {
        this.languageIndicator.style.display = 'none';
    }

    clearSearch() {
        this.searchInput.value = '';
        this.clearBtn.style.display = 'none';
        this.hideLanguageIndicator();
        this.hideResult();
        this.hideError();
        this.focusInput();
    }

    async handleSearch() {
        const word = this.searchInput.value.trim();
        
        if (!word) {
            this.showError('Please enter a word to translate');
            return;
        }

        this.showLoading();
        this.hideResult();
        this.hideError();

        try {
            const result = await this.translateWord(word);
            this.hideLoading();
            
            if (result.success) {
                this.showResult(result);
            } else {
                this.showError(result.message || 'Translation failed');
            }
        } catch (error) {
            this.hideLoading();
            console.error('Translation error:', error);
            this.showError('Network error. Please check your connection and try again.');
        }
    }

    async translateWord(word) {
        const response = await fetch(`${this.apiUrl}?word=${encodeURIComponent(word)}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    }

    showResult(result) {
        this.resultInput.textContent = result.input;
        this.resultOutput.textContent = result.translation;
        
        // Determine source
        const isLocalResult = this.isLikelyLocalResult(result.input, result.translation);
        this.sourceBadge.textContent = isLocalResult ? 'Local Database' : 'Baidu Translate API';
        this.sourceBadge.style.background = isLocalResult ? '#d4edda' : '#fff3cd';
        this.sourceBadge.style.color = isLocalResult ? '#155724' : '#856404';
        
        this.resultContainer.style.display = 'block';
        
        // Scroll to result
        this.resultContainer.scrollIntoView({ 
            behavior: 'smooth', 
            block: 'start' 
        });
    }

    isLikelyLocalResult(input, translation) {
        // Simple heuristic: if the result matches known local entries
        const localPairs = [
            { en: 'hello', zh: '你好' },
            { en: 'test', zh: '测试' }
        ];
        
        return localPairs.some(pair => 
            (pair.en.toLowerCase() === input.toLowerCase() && pair.zh === translation) ||
            (pair.zh === input && pair.en.toLowerCase() === translation.toLowerCase())
        );
    }

    hideResult() {
        this.resultContainer.style.display = 'none';
    }

    showLoading() {
        this.loading.style.display = 'block';
    }

    hideLoading() {
        this.loading.style.display = 'none';
    }

    showError(message) {
        this.errorMessage.textContent = message;
        this.errorContainer.style.display = 'block';
    }

    hideError() {
        this.errorContainer.style.display = 'none';
    }
}

// Initialize the app when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new DictionaryApp();
});

// Add some utility functions for better UX
document.addEventListener('keydown', (e) => {
    // Press '/' to focus search input
    if (e.key === '/' && !e.ctrlKey && !e.metaKey && !e.altKey) {
        e.preventDefault();
        document.getElementById('searchInput').focus();
    }
    
    // Press 'Escape' to clear search
    if (e.key === 'Escape') {
        const app = window.dictionaryApp;
        if (app) {
            app.clearSearch();
        }
    }
});

// Add visual feedback for button clicks
document.addEventListener('click', (e) => {
    if (e.target.matches('button, .example-tag')) {
        e.target.style.transform = 'scale(0.95)';
        setTimeout(() => {
            e.target.style.transform = '';
        }, 150);
    }
});

// Store app instance globally for debugging
document.addEventListener('DOMContentLoaded', () => {
    window.dictionaryApp = new DictionaryApp();
});