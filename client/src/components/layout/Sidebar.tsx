import { Link, useLocation } from "wouter";
import { X, Home, Users, Layers, Bell, Settings, MapPin, LogOut } from "lucide-react";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { useAuth } from "@/contexts/AuthContext";
import { api } from "@/services/api";
import { useToast } from "@/hooks/use-toast";

interface SidebarProps {
  onClose?: () => void;
}

const navigationItems = [
  { href: "/dashboard", label: "Dashboard", icon: Home, active: true },
  { href: "/namhattas", label: "Namhattas", icon: Home },
  { href: "/devotees", label: "Devotees", icon: Users },
  { href: "/updates", label: "Updates", icon: Bell },
  { href: "/map", label: "Map View", icon: MapPin },
  { href: "/statuses", label: "Statuses", icon: Layers },
];

export default function Sidebar({ onClose }: SidebarProps) {
  const [location] = useLocation();
  const { user, logout } = useAuth();
  const { toast } = useToast();

  const handleLogout = async () => {
    try {
      // Call logout from auth context
      logout();
      toast({
        title: "Logged out successfully",
        description: "You have been logged out of the system.",
      });
    } catch (error) {
      toast({
        title: "Logout failed",
        description: "There was an error logging you out.",
        variant: "destructive",
      });
    }
  };

  const getUserDisplayName = () => {
    if (!user) return "Guest User";
    return user.username;
  };

  const getUserRoleDisplay = () => {
    if (!user) return "Guest";
    switch (user.role) {
      case 'ADMIN':
        return 'System Administrator';
      case 'OFFICE':
        return 'Office Staff';
      case 'DISTRICT_SUPERVISOR':
        return 'District Supervisor';
      default:
        return 'User';
    }
  };

  const getUserInitials = () => {
    if (!user) return "GU";
    return user.username.substring(0, 2).toUpperCase();
  };

  return (
    <div className="flex-1 flex flex-col min-h-0 glass-card border-0 border-r border-white/20 dark:border-slate-700/50">
      {/* Logo Section */}
      <div className="flex items-center h-20 flex-shrink-0 px-6 border-b border-white/20 dark:border-slate-700/50">
        {onClose && (
          <Button
            variant="ghost"
            size="icon"
            onClick={onClose}
            className="mr-2 lg:hidden"
          >
            <X className="h-5 w-5" />
          </Button>
        )}
        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-xl flex items-center justify-center">
            <span className="text-white text-lg font-bold">ॐ</span>
          </div>
          <div>
            <h1 className="text-xl font-bold gradient-text">Namhatta</h1>
            <p className="text-xs text-gray-500 dark:text-gray-400">Management System</p>
          </div>
        </div>
      </div>

      {/* Navigation Menu */}
      <nav className="flex-1 px-4 py-6 space-y-2 overflow-y-auto">
        {navigationItems.map((item) => {
          const isActive = location === item.href || (location === "/" && item.href === "/dashboard");
          const Icon = item.icon;
          
          return (
            <Link key={item.href} href={item.href}>
              <div
                className={cn(
                  "nav-item group cursor-pointer",
                  isActive ? "nav-item-active" : "nav-item-inactive"
                )}
                onClick={onClose}
              >
                <Icon className="mr-3 h-5 w-5" />
                {item.label}
              </div>
            </Link>
          );
        })}
      </nav>

      {/* User Profile Section */}
      <div className="flex-shrink-0 p-3 border-t border-white/20 dark:border-slate-700/50">
        <div className="flex items-start space-x-3 p-3 rounded-xl bg-white/50 dark:bg-slate-700/50">
          <Avatar className="flex-shrink-0">
            <AvatarFallback className="bg-gradient-to-br from-emerald-400 to-teal-500 text-white text-sm font-semibold">
              {getUserInitials()}
            </AvatarFallback>
          </Avatar>
          <div className="flex-1 min-w-0 space-y-1">
            <p className="text-sm font-medium text-gray-900 dark:text-white truncate">
              {getUserDisplayName()}
            </p>
            <p className="text-xs text-gray-500 dark:text-gray-400 truncate">
              {getUserRoleDisplay()}
            </p>
            
            {/* Show districts for District Supervisors */}
            {user?.role === 'DISTRICT_SUPERVISOR' && user?.districts && user.districts.length > 0 && (
              <div className="pt-2 border-t border-white/20 dark:border-slate-700/50">
                <p className="text-xs font-medium text-gray-600 dark:text-gray-300 mb-1">
                  Assigned Districts:
                </p>
                <div className="space-y-1">
                  {user.districts.slice(0, 2).map((district, index) => (
                    <div key={index} className="text-xs text-gray-500 dark:text-gray-400 bg-gray-100 dark:bg-gray-700/50 px-2 py-1 rounded truncate">
                      {typeof district === 'string' ? district : district.name}
                    </div>
                  ))}
                  {user.districts.length > 2 && (
                    <div className="text-xs text-gray-400 dark:text-gray-500">
                      +{user.districts.length - 2} more
                    </div>
                  )}
                </div>
              </div>
            )}
          </div>
          <Button 
            variant="ghost" 
            size="icon" 
            className="flex-shrink-0 text-red-500 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-900/20"
            onClick={handleLogout}
            title="Sign Out"
          >
            <LogOut className="h-4 w-4" />
          </Button>
        </div>
      </div>
    </div>
  );
}
