self.addEventListener("push", () => {
  self.registration.showNotification("JobWatch", {
    body: "A new job was posted at one of your watchlisted companies.",
    icon: "/icon.png",
  });
});

self.addEventListener("notificationclick", (event) => {
  event.notification.close();
  event.waitUntil(clients.openWindow("/jobs"));
});
