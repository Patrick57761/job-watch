self.addEventListener("push", (event) => {
  let title = "JobWatch";
  let body = "A new job was posted.";
  let icon = "/icon.png";
  let url = "/jobs";

  try {
    const data = event.data.json();
    title = data.title || title;
    body = data.body || body;
    icon = data.icon || icon;
    url = data.url || url;
  } catch (e) {}

  event.waitUntil(
    self.registration.showNotification(title, { body, icon, data: { url } })
  );
});

self.addEventListener("notificationclick", (event) => {
  event.notification.close();
  event.waitUntil(clients.openWindow(event.notification.data.url));
});
