#!/bin/bash
export JWT_SECRET="42d236149a7fe69b8f2f5ec7093f4805873e6569098cacbdc076eae0f80eef53"
export SESSION_SECRET="5fcddc0a4c6ed316629c871d768422995efc66aff8fa0c658c1f0006db3c2351"

echo "Starting Namhatta Management System Spring Boot Application..."
echo "Database: PostgreSQL (Neon)"
echo "Port: 5000"
echo "Profile: development"
java -jar target/namhatta-management-system-1.0.0.jar --spring.profiles.active=development --server.address=0.0.0.0 --server.port=5000