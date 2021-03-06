import { AuthService, OpenAPI } from './openapi';

const ACCESS_TOKEN = 'accessToken';
const REFRESH_TOKEN = 'refreshToken';

export const TokenService = {
  saveAccessToken: (token: string) => {
    sessionStorage.setItem(ACCESS_TOKEN, token);
    OpenAPI.TOKEN = token;
  },
  getAccessToken: (): string => {
    return sessionStorage.getItem(ACCESS_TOKEN)!;
  },
  removeAccessToken: () => {
    sessionStorage.removeItem(ACCESS_TOKEN);
    OpenAPI.TOKEN = undefined;
  },
  saveRefreshToken: (token: string) => {
    localStorage.setItem(REFRESH_TOKEN, token);
  },
  getRefreshToken: (): string => {
    return localStorage.getItem(REFRESH_TOKEN)!;
  },
  removeRefreshToken: () => {
    localStorage.removeItem(REFRESH_TOKEN);
  },
  isAdmin: () => {
    return sessionStorage.getItem(ACCESS_TOKEN)!;
  },

  getAccessTokenWithRefreshToken: () => {
    AuthService.refreshToken().then(() => {
      console.log('refresh');
    });
  },

  refreshToken: async () => {
    try {
      OpenAPI.TOKEN = localStorage.getItem(REFRESH_TOKEN)!;
      const authToken = await AuthService.refreshToken();
      TokenService.saveAccessToken(`${authToken}`);
    } catch (error) {
      console.log('Refresh Token Error');
    }
  },
};
