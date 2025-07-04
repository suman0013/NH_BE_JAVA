// API Configuration utility for dynamic backend URL switching

/**
 * Get the API base URL from environment variables
 * Defaults to localhost:5000 for development
 * Can be overridden with VITE_API_BASE_URL environment variable
 */
export const getApiBaseUrl = (): string => {
  return import.meta.env.VITE_API_BASE_URL || 'http://localhost:5000';
};

/**
 * Build complete API URL by combining base URL with endpoint
 * @param endpoint - API endpoint (e.g., '/api/devotees')
 * @returns Full API URL
 */
export const buildApiUrl = (endpoint: string): string => {
  const baseUrl = getApiBaseUrl();
  // Remove trailing slash from base URL and leading slash from endpoint if both exist
  const cleanBaseUrl = baseUrl.replace(/\/$/, '');
  const cleanEndpoint = endpoint.startsWith('/') ? endpoint : `/${endpoint}`;
  
  return `${cleanBaseUrl}${cleanEndpoint}`;
};

/**
 * API configuration object
 */
export const API_CONFIG = {
  baseUrl: getApiBaseUrl(),
  timeout: 10000, // 10 seconds
  defaultHeaders: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
};

/**
 * Check if we're running in development mode
 */
export const isDevelopment = (): boolean => {
  return import.meta.env.MODE === 'development';
};

/**
 * Log API configuration (useful for debugging)
 */
export const logApiConfig = (): void => {
  if (isDevelopment()) {
    console.log('🚀 API Configuration:', {
      baseUrl: getApiBaseUrl(),
      mode: import.meta.env.MODE,
      isDev: isDevelopment(),
    });
  }
};