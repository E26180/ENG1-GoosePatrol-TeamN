"""Turns one of our Markdown notes into a plain web page for the team website.

Usage: python3 tools/md_page.py <note.md> <page.html>   (standard library only)

Handles only what our notes use: headings, paragraphs, bullet and numbered lists, tables,
images, links, **bold**, *italic* and `code`. Links to other .md notes are changed to .html.
The website build (.github/workflows/deploy-website.yml) runs it for docs/testing.
"""
import html
import re
import sys

STYLE = """
body { margin: 0; background: #f5f2e8; color: #1e2b26; line-height: 1.6;
       font-family: system-ui, -apple-system, "Segoe UI", Roboto, Arial, sans-serif; }
main { max-width: 960px; margin: 0 auto; padding: 32px 16px 64px; }
a { color: #b85f0a; }
table { border-collapse: collapse; width: 100%; margin: 12px 0; font-size: 0.95rem; display: block; overflow-x: auto; }
th, td { border: 1px solid #d8d1bd; padding: 6px 8px; text-align: left; vertical-align: top; }
th { background: #ece6d5; }
img { max-width: 100%; height: auto; border: 1px solid #d8d1bd; border-radius: 6px; }
code { background: #ece6d5; padding: 0 4px; border-radius: 4px; overflow-wrap: anywhere; }
@media (prefers-color-scheme: dark) {
  body { background: #1e2b26; color: #f5f2e8; }
  a { color: #f2a541; }
  th { background: #26362f; }
  th, td, img { border-color: #3a4a42; }
  code { background: #26362f; }
}
"""


def inline(text):
    text = html.escape(text, quote=False)
    text = re.sub(r"`([^`]+)`", r"<code>\1</code>", text)
    text = re.sub(r"!\[([^\]]*)\]\(([^)]+)\)", r'<img src="\2" alt="\1">', text)
    text = re.sub(r"\[([^\]]+)\]\(([^)]+?)\.md\)", r'<a href="\2.html">\1</a>', text)
    text = re.sub(r"\[([^\]]+)\]\(([^)]+)\)", r'<a href="\2">\1</a>', text)
    text = re.sub(r"\*\*([^*]+)\*\*", r"<strong>\1</strong>", text)
    text = re.sub(r"(?<![\w*])\*([^*]+)\*(?![\w*])", r"<em>\1</em>", text)
    return text


def cells(row):
    return [c.strip() for c in row.strip().strip("|").split("|")]


def convert(lines):
    out, title, i = [], "Notes", 0
    while i < len(lines):
        line = lines[i].rstrip()
        if not line.strip():
            i += 1
        elif line.startswith("#"):
            level = len(line) - len(line.lstrip("#"))
            text = line[level:].strip()
            if level == 1:
                title = text
            out.append(f"<h{level}>{inline(text)}</h{level}>")
            i += 1
        elif line.startswith("|"):
            head = cells(line)
            i += 2  # skip the |---| line
            out.append("<table><thead><tr>" + "".join(f"<th>{inline(c)}</th>" for c in head) + "</tr></thead><tbody>")
            while i < len(lines) and lines[i].startswith("|"):
                out.append("<tr>" + "".join(f"<td>{inline(c)}</td>" for c in cells(lines[i])) + "</tr>")
                i += 1
            out.append("</tbody></table>")
        elif re.match(r"\s*(- |\d+\. )", line):
            tag = "ol" if re.match(r"\s*\d+\. ", line) else "ul"
            out.append(f"<{tag}>")
            while i < len(lines) and re.match(r"\s*(- |\d+\. )", lines[i]):
                parts = [re.sub(r"^\s*(- |\d+\. )", "", lines[i].rstrip())]
                i += 1
                while i < len(lines):
                    if lines[i].startswith("  ") and not re.match(r"\s*(- |\d+\. )", lines[i]):
                        parts[-1] += " " + lines[i].strip()  # the line goes on
                        i += 1
                    elif not lines[i].strip() and i + 1 < len(lines) and lines[i + 1].startswith("  "):
                        parts.append(lines[i + 1].strip())  # an indented paragraph of the same item
                        i += 2
                    else:
                        break
                if i < len(lines) and not lines[i].strip():
                    i += 1
                out.append("<li>" + "".join(f"<p>{inline(x)}</p>" if n else inline(x) for n, x in enumerate(parts)) + "</li>")
            out.append(f"</{tag}>")
        else:
            para = []
            while i < len(lines) and lines[i].strip() and not lines[i].startswith(("#", "|")) \
                    and not re.match(r"\s*(- |\d+\. )", lines[i]):
                para.append(lines[i].strip())
                i += 1
            out.append("<p>" + inline(" ".join(para)) + "</p>")
    return title, "\n".join(out)


if __name__ == "__main__":
    src, dst = sys.argv[1], sys.argv[2]
    with open(src, encoding="utf-8") as f:
        title, body = convert(f.read().split("\n"))
    page = (f'<!doctype html>\n<html lang="en">\n<head>\n<meta charset="utf-8">\n'
            f'<meta name="viewport" content="width=device-width, initial-scale=1">\n'
            f"<title>{html.escape(title)} · Goose Patrol</title>\n<style>{STYLE}</style>\n</head>\n<body>\n<main>\n"
            f'<p><a href="../index.html">Goose Patrol home</a></p>\n{body}\n</main>\n</body>\n</html>\n')
    with open(dst, "w", encoding="utf-8") as f:
        f.write(page)
    print("wrote", dst)
