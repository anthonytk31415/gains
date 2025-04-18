import asyncpg
from typing import Optional


# This file serves as a central manager for database connections in your application. 
# Key purposes:
# - Connect to the database
# - Disconnect from the database
# - Get a connection from the pool
# - Release a connection back to the pool


class Database:
    def __init__(self):
        self.pool: Optional[asyncpg.Pool] = None

    async def connect(self, dsn: str):
        """
        Create a connection pool to the database
        """
        self.pool = await asyncpg.create_pool(dsn)

    async def disconnect(self):
        """
        Close the database connection pool
        """
        if self.pool:
            await self.pool.close()

    async def get_connection(self):
        """
        Get a connection from the pool
        """
        if not self.pool:
            raise Exception("Database not connected")
        return await self.pool.acquire()

    async def release_connection(self, conn):
        """
        Release a connection back to the pool
        """
        if self.pool:
            await self.pool.release(conn)

# Create a global database instance
db = Database() 