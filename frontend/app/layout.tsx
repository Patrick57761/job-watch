import type { Metadata } from "next";
import "./globals.css";
import ThemeProvider from "./components/ThemeProvider";

export const metadata: Metadata = {
  title: "JobWatch",
  description: "Get notified when your target companies post new jobs.",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body className="bg-gray-50 dark:bg-gray-950 text-gray-900 dark:text-gray-100 antialiased">
        <ThemeProvider />
        {children}
      </body>
    </html>
  );
}
