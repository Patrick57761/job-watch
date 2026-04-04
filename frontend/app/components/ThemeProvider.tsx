"use client";

import { useEffect } from "react";

export default function ThemeProvider() {
  useEffect(() => {
    const dark = localStorage.getItem("theme") === "dark";
    document.documentElement.classList.toggle("dark", dark);
  }, []);

  return null;
}
