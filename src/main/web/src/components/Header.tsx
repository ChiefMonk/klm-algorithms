import { cn } from "@/lib/utils";
import { Logo } from "./ui/logo";
import {
  NavigationMenu,
  NavigationMenuItem,
  NavigationMenuList,
  navigationMenuTriggerStyle,
} from "@/components/ui/navigation-menu";
import { NavLink } from "react-router-dom";

interface HeaderProps {
  className?: string;
}

function Header({ className }: HeaderProps) {
  return (
    <header
      className={cn(
        "text-center p-4 sm:px-6 border-b flex items-center justify-center w-full",
        className
      )}
    >
      <div className="max-w-screen-xl flex items-center justify-between w-full ">
        <div className="flex items-center gap-2">
          <NavLink to="/">
            <Logo className="w-8" />
          </NavLink>
        </div>
        <div>
          <NavigationMenu>
            <NavigationMenuList>
              <NavigationMenuItem>
                <NavLink to="/syntax" className={navigationMenuTriggerStyle()}>
                  Syntax
                </NavLink>
              </NavigationMenuItem>
              <NavigationMenuItem>
                <NavLink
                  to="/knowledge-base"
                  className={navigationMenuTriggerStyle()}
                >
                  Knolwedge Base
                </NavLink>
              </NavigationMenuItem>
            </NavigationMenuList>
          </NavigationMenu>
        </div>
      </div>
    </header>
  );
}

export { Header };
