import logo from "@/assets/logo.svg";
import React from "react";

interface LogoProps extends React.ImgHTMLAttributes<HTMLImageElement> {
  src?: string;
  alt?: string;
}

const Logo: React.FC<LogoProps> = ({ src = logo, alt = "Logo", ...rest }) => {
  return <img src={src} alt={alt} {...rest} />;
};

export { Logo };
