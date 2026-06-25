export interface GuildChatParams { page?: number; size?: number }
export interface SendGuildChatRequest { message: string }
export interface GuildChatMessage { chatId: number; guildId: number; userId: number; nickname: string; profileImageUrl?: string; characterId?: number; characterName?: string; characterLevel?: number; messageType?: string; message: string; createdAt: string; isMe?: boolean }
export interface GuildChatListResponse { guildId: number; chats: GuildChatMessage[]; page: number; size: number; hasNext: boolean }
