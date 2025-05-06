// API配置
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/ishareReading'

export const API = {
  // 图形验证码
  CAPTCHA: (uuid) => `${API_BASE_URL}/login/captcha.jpg/${uuid}`,
  // 发送邮箱验证码
  SEND_EMAIL_CODE: `${API_BASE_URL}/login/getCode`,
  // 注册
  REGISTER: `${API_BASE_URL}/login/register`,
  // 登录
  LOGIN: `${API_BASE_URL}/login`,
  // 用户个人中心
  USER_INFO: (userId) => `${API_BASE_URL}/user/personalInfo?userId=${userId}`,
  // 上传头像
  UPLOAD_AVATAR: `${API_BASE_URL}/user/uploadAvatar`,
  // 获取书籍类型
  GET_BOOK_TYPES: `${API_BASE_URL}/type/listByType?type=books`,
  GET_BOOK_METADATA: `${API_BASE_URL}/book/getBooksMetadata`,
  IMPORT_BOOK: `${API_BASE_URL}/book/import`,
  GET_HOT_BOOKS: `${API_BASE_URL}/index/hot/rank/book`,
  GET_LATEST_BOOKS: `${API_BASE_URL}/index/book/release`,
  GET_FEATURED_BOOKS: `${API_BASE_URL}/book/getBooksPages`,
  GET_BOOK_HOMEPAGE_DETAIL: (id) => `${API_BASE_URL}/book/getBooksHomePageById?id=${id}`,  // 获取精选好书详情
  PUSH_BOOKS: `${API_BASE_URL}/userModel/pushBooks`, // 获取推荐书籍
  GET_BOOK_READING_DETAILS: `${API_BASE_URL}/book/getBooksImgByPage`, // 获取阅读界面详情
  
  // 聊天相关接口
  CHAT: {
    // 创建新会话
    CREATE_SESSION: `${API_BASE_URL}/chat/session/create`,
    // 获取会话列表
    GET_SESSIONS: `${API_BASE_URL}/chat/session/list`,
    // 获取会话消息历史
    GET_MESSAGES: (sessionId) => `${API_BASE_URL}/chat/message/list/${sessionId}`,
    // 发送消息
    SEND_MESSAGE: `${API_BASE_URL}/chat/message/send`,
    // 删除会话
    DELETE_SESSION: (sessionId) => `${API_BASE_URL}/chat/session/delete/${sessionId}`,
    // 更新会话标题
    UPDATE_SESSION_TITLE: `${API_BASE_URL}/chat/session/update/title`,
    // 流式响应
    STREAM_RESPONSE: `${API_BASE_URL}/chat/message/stream`,
    // 聊天测试接口
    CHAT_TEST: (content, sessionId) => `${API_BASE_URL}/chat/chatTest?content=${encodeURIComponent(content)}&sessionId=${sessionId}`
  },

  // 知识库相关接口
  KNOWLEDGE: {
    // 导入知识库
    IMPORT_RESOURCES: `${API_BASE_URL}/rag/importResources`
  },

  // AI助手相关接口
  AGENTS: {
    // 获取AI助手信息
    GET_AGENTS: (name) => `${API_BASE_URL}/agents/getAgents?name=${name}`
  }
} 