import React from "react";

interface DividerProps {
  length?: string; 
  thickness?: string; 
  className?: string; 
}

const Divider: React.FC<DividerProps> = ({
  length = "full",
  thickness = "1",
  className = "bg-black",
}) => {
  const widthClass = `w-${length}`;
  const heightClass = `h-[${thickness}px]`;

  return (
    <hr className="w-full max-w-sm border border-gray-300 my-[20px]" />
  );
};

export default Divider;
