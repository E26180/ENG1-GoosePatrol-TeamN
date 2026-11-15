// Shows every file the team has put in one docs folder (docs/architecture or docs/plan).
// The website build writes <folder>/files.json with the file names, so a new diagram or plan
// snapshot appears here as soon as it is merged into main; nobody has to edit this page.
// Pictures (svg, png, jpg) are shown on the page; any other file (pdf, docx, ...) gets a link.
function showGallery(folder, targetId) {
  const target = document.getElementById(targetId);
  const pictures = /\.(svg|png|jpe?g|gif|webp)$/i;

  fetch(folder + "/files.json")
    .then(response => response.ok ? response.json() : [])
    .catch(() => [])
    .then(files => {
      if (files.length === 0) {
        return; // keep the "nothing yet" message that is already in the page
      }
      target.textContent = "";
      for (const name of files) {
        const path = folder + "/" + encodeURIComponent(name);
        const card = document.createElement("div");
        card.className = "card";

        const title = document.createElement("h3");
        title.textContent = name.replace(/\.[^.]+$/, "").replace(/[-_]+/g, " ");
        card.appendChild(title);

        const link = document.createElement("a");
        link.href = path;
        link.textContent = pictures.test(name) ? "Open full size" : "Open " + name;
        const line = document.createElement("p");
        line.appendChild(link);
        card.appendChild(line);

        if (pictures.test(name)) {
          const img = document.createElement("img");
          img.className = "figure";
          img.src = path;
          img.alt = title.textContent;
          img.loading = "lazy";
          card.appendChild(img);
        }
        target.appendChild(card);
      }
    });
}
