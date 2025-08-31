import { cn } from "@/lib/utils";
import {
  NavigationMenu,
  NavigationMenuItem,
  NavigationMenuList,
  navigationMenuTriggerStyle,
} from "@/components/ui/navigation-menu";
import { NavLink } from "react-router-dom";
import { SCREEN_WIDTH_SIZE } from "@/lib/constants";
import { LogoIcon } from "./ui/logo-icon";

interface HeaderProps {
  className?: string;
}

function Header({ className }: HeaderProps) {
  const activeNavItem = ({ isActive }: { isActive: boolean }) => {
    return cn(
      navigationMenuTriggerStyle(),
      "bg-transparent font-semibold text-foreground hover:text-white hover:bg-app-4/90 focus:bg-app-4/90 focus:text-white",
      "border-2 border-app-4 hover:border-app-4/90 focus:border-app-4/90",
      isActive && "bg-app-4 text-white"
    );
  };
  return (
    <header
      className={cn(
        "text-center border-b-[6px] border-app-4 flex items-center justify-center w-full",
        "text-foreground shadow bg-app-2",
        className
      )}
    >
      <div
        className={cn(
          "grid grid-cols-3 items-center justify-between max-w-screen-xl w-full p-4 sm:px-6",
          `max-w-[${SCREEN_WIDTH_SIZE}vw]`
        )}
      >
        <div className="flex items-center gap-2">
          <NavLink to="/" className="flex items-center gap-2">
            <LogoIcon className="w-16 h-16 fill-slate-900 text-slate-900" />
          </NavLink>
        </div>
        <div className="text-center">
          <h1 className="text-2xl font-bold">KLM-Style Defeasible Reasoning</h1>
          <p className="text-sm font-medium text-muted-foreground">
            An Implementation and Evaluation of Entailment and Justification
            Algorithms
          </p>
        </div>
        <div className="justify-self-end">
          <NavigationMenu>
            <NavigationMenuList>
              <NavigationMenuItem>
                <NavLink to="/" className={activeNavItem}>
                  Entailment
                </NavLink>
              </NavigationMenuItem>
              <NavigationMenuItem>
                <NavLink to="/justification" className={activeNavItem}>
                  Justification
                </NavLink>
              </NavigationMenuItem>
              <NavigationMenuItem>
                <NavLink to="/evaluation" className={activeNavItem}>
                  Evaluation
                </NavLink>
              </NavigationMenuItem>
              {/* <NavigationMenuItem>
                <NavLink to="/syntax" className={activeNavItem}>
                  Syntax
                </NavLink>
              </NavigationMenuItem>
              <NavigationMenuItem>
                <NavLink
                  to="/knowledge-base"
                  className={activeNavItem}
                >
                  Knolwedge Base
                </NavLink>
              </NavigationMenuItem> */}
              <NavigationMenuItem>
                <NavLink to="/info" className={activeNavItem}>
                  Info
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
