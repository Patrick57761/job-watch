"use client";

import { useState, useEffect, FormEvent } from "react";
import { useRouter } from "next/navigation";
import { login, register } from "@/lib/api";

export default function AuthPage() {
  const router = useRouter();

  useEffect(() => {
    if (localStorage.getItem("token")) {
      router.replace("/jobs");
    }
  }, []);
  const [mode, setMode] = useState<"login" | "register">("login");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const fn = mode === "login" ? login : register;
      const { token } = await fn(username, password);
      localStorage.setItem("token", token);
      router.push("/jobs");
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Something went wrong.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="min-h-screen flex items-center justify-center px-4 bg-gray-50 dark:bg-gray-950">
      <div className="w-full max-w-sm">
        {/* Logo / title */}
        <div className="mb-8 text-center">
          <h1 className="text-3xl font-bold text-gray-900 dark:text-gray-100">JobWatch</h1>
          <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
            Get notified when your target companies post new jobs.
          </p>
        </div>

        {/* Card */}
        <div className="bg-white dark:bg-gray-900 rounded-2xl shadow-sm border border-gray-200 dark:border-gray-800 p-8">
          {/* Mode toggle */}
          <div className="flex mb-6 rounded-lg bg-gray-100 dark:bg-gray-800 p-1">
            <button
              type="button"
              onClick={() => { setMode("login"); setError(""); }}
              className={`flex-1 py-1.5 text-sm font-medium rounded-md transition-colors ${
                mode === "login"
                  ? "bg-white dark:bg-gray-700 shadow-sm text-gray-900 dark:text-gray-100"
                  : "text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200"
              }`}
            >
              Log in
            </button>
            <button
              type="button"
              onClick={() => { setMode("register"); setError(""); }}
              className={`flex-1 py-1.5 text-sm font-medium rounded-md transition-colors ${
                mode === "register"
                  ? "bg-white dark:bg-gray-700 shadow-sm text-gray-900 dark:text-gray-100"
                  : "text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200"
              }`}
            >
              Register
            </button>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label htmlFor="username" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                Username
              </label>
              <input
                id="username"
                type="text"
                required
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="yourname"
                className="w-full px-3 py-2 border border-gray-300 dark:border-gray-700 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 placeholder:text-gray-400 dark:placeholder:text-gray-600"
              />
              {mode === "register" && (
                <p className="text-xs text-gray-400 dark:text-gray-500 mt-1">At least 3 characters, no spaces.</p>
              )}
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                Password
              </label>
              <input
                id="password"
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full px-3 py-2 border border-gray-300 dark:border-gray-700 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 placeholder:text-gray-400 dark:placeholder:text-gray-600"
              />
            </div>

            {error && (
              <p className="text-sm text-red-600 dark:text-red-400 bg-red-50 dark:bg-red-950 border border-red-200 dark:border-red-900 rounded-lg px-3 py-2">
                {error}
              </p>
            )}

            <button
              type="submit"
              disabled={loading}
              className="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white text-sm font-medium rounded-lg transition-colors"
            >
              {loading
                ? "Please wait…"
                : mode === "login"
                ? "Log in"
                : "Create account"}
            </button>
          </form>
        </div>
      </div>
    </main>
  );
}
