package main

import (
	"log"
	"os"

	"taskservice/internal/api"
	"taskservice/internal/config"
	"taskservice/internal/database"
	"taskservice/pkg/logger"

	"github.com/joho/godotenv"
)

// @title Task Management API
// @version 1.0
// @description A RESTful API for managing tasks with PostgreSQL backend
// @termsOfService http://swagger.io/terms/

// @contact.name API Support
// @contact.url http://www.taskservice.io/support
// @contact.email support@taskservice.io

// @license.name MIT
// @license.url https://opensource.org/licenses/MIT

// @host localhost:8080
// @BasePath /api/v1
// @schemes http https

func main() {
	// Load environment variables
	if err := godotenv.Load(); err != nil {
		log.Println("No .env file found, using system environment variables")
	}

	// Initialize logger
	logger := logger.New()

	// Load configuration
	cfg := config.Load()
	logger.Info("Configuration loaded", "port", cfg.Port, "database", cfg.Database.Host)

	// Initialize database
	db, err := database.NewConnection(cfg.Database)
	if err != nil {
		logger.Error("Failed to connect to database", "error", err)
		os.Exit(1)
	}
	defer db.Close()

	// Run migrations
	if err := database.RunMigrations(cfg.Database); err != nil {
		logger.Error("Failed to run migrations", "error", err)
		os.Exit(1)
	}

	// Initialize API server
	server := api.NewServer(db, logger, cfg)

	// Start server
	logger.Info("Starting server", "port", cfg.Port)
	if err := server.Run(":" + cfg.Port); err != nil {
		logger.Error("Server failed to start", "error", err)
		os.Exit(1)
	}
}