const topViewEl = document.querySelector("#topView");
const sideViewEl = document.querySelector("#sideView");

function renderCarportDrawing() {
    if (!topViewEl || !sideViewEl) {
        return;
    }

    const carport = readCarportFromForm();

    sideViewEl.innerHTML = createSideView(carport);
    topViewEl.innerHTML = createTopView(carport);
}

function readCarportFromForm() {
    return {
        width: readNumber("#carportWidth", 600),
        length: readNumber("#carportLength", 780),
        height: readNumber("#carportHeight", 230),
        hasShed: readShedChoice(),
        shedWidth: readNumber("#shedWidth", 210),
        shedLength: readNumber("#shedLength", 210)
    };
}

function readNumber(selector, fallback) {
    const element = document.querySelector(selector);

    if (!element || element.value === "") {
        return fallback;
    }

    return Number(element.value);
}

function readShedChoice() {
    const element = document.querySelector("#shedChoice");

    if (!element) {
        return true;
    }

    return element.value === "yes" || element.value === "Med redskabsrum";
}

function createSideView(carport) {
    const scale = 1.05;
    const x = 120;
    const roofY = 58;
    const groundY = 245;
    const length = carport.length * scale;
    const height = carport.height * 0.72;
    const roofFall = 20;
    const postTopY = groundY - height + 18;
    const shedLength = carport.hasShed ? Math.min(carport.shedLength * scale, length * 0.35) : 0;
    const shedX = x + length - shedLength - 28;
    const svgWidth = x + length + 130;

    let svg = `
        <svg viewBox="0 0 ${svgWidth} 330" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="Sidevisning af carport">
            ${svgDefs()}
            <rect width="100%" height="100%" fill="#ffffff"/>
            <line x1="${x - 40}" y1="${groundY}" x2="${x + length + 35}" y2="${groundY}" class="draw-line draw-heavy"/>
            <polyline points="${x - 8},${roofY + roofFall} ${x + 40},${roofY + 10} ${x + length},${roofY + 8} ${x + length + 16},${roofY + 26}" class="draw-line draw-roof"/>
            <polyline points="${x - 2},${roofY + roofFall + 12} ${x + 44},${roofY + 22} ${x + length},${roofY + 20}" class="draw-line draw-light"/>
            ${createSidePosts(x, postTopY, groundY, length)}
            ${createSideRoofBoards(x + 30, roofY + 17, length - 70)}
            ${carport.hasShed ? createShedSideView(shedX, postTopY + 6, groundY, shedLength) : ""}
            ${createHorizontalDimension(x, groundY + 52, x + length, formatMeasure(carport.length))}
            ${createHorizontalDimension(x + 1, groundY + 28, shedX, formatMeasure(Math.max(0, Math.round((shedX - x) / scale))))}
            ${carport.hasShed ? createHorizontalDimension(shedX, groundY + 28, shedX + shedLength, formatMeasure(carport.shedLength)) : ""}
            ${createVerticalDimension(x - 86, postTopY, groundY, formatMeasure(carport.height))}
            ${createVerticalDimension(x + length + 70, postTopY + 12, groundY, formatMeasure(Math.max(180, carport.height - 10)))}
        </svg>
    `;

    return svg;
}

function createTopView(carport) {
    const scale = 1.05;
    const x = 120;
    const y = 82;
    const length = carport.length * scale;
    const width = carport.width * scale;
    const shedLength = carport.hasShed ? Math.min(carport.shedLength * scale, length * 0.35) : 0;
    const shedWidth = carport.hasShed ? Math.min(carport.shedWidth * scale, width * 0.55) : 0;
    const shedX = x + length - shedLength - 28;
    const shedY = y + width - shedWidth - 28;
    const svgWidth = x + length + 130;
    const svgHeight = y + width + 105;

    let svg = `
        <svg viewBox="0 0 ${svgWidth} ${svgHeight}" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="Plantegning af carport">
            ${svgDefs()}
            <rect width="100%" height="100%" fill="#ffffff"/>
            <rect x="${x}" y="${y}" width="${length}" height="${width}" class="draw-fill-none draw-line draw-heavy"/>
            ${createRafters(x, y, length, width)}
            ${createBeams(x, y, length, width)}
            ${createTopPosts(x, y, length, width, shedX)}
            ${createDiagonalBracing(x + 58, y + 58, shedX - 18, y + width - 58)}
            ${carport.hasShed ? createShedTopView(shedX, shedY, shedLength, shedWidth) : ""}
            ${createRafterSpacingDimensions(x, y - 34, length)}
            ${createHorizontalDimension(x, y + width + 58, x + length, formatMeasure(carport.length))}
            ${createVerticalDimension(x - 64, y, y + width, formatMeasure(carport.width))}
            ${createVerticalDimension(x - 34, y + 42, y + width - 42, formatMeasure(Math.max(0, carport.width - 70)))}
        </svg>
    `;

    return svg;
}

function createSidePosts(x, topY, groundY, length) {
    const positions = [
        x + 110,
        x + length * 0.54,
        x + length * 0.70,
        x + length - 28
    ];

    return positions.map(position => `
        <rect x="${position - 5}" y="${topY}" width="10" height="${groundY - topY}" class="draw-fill-white draw-line draw-heavy"/>
    `).join("");
}

function createSideRoofBoards(x, y, length) {
    let svg = "";

    for (let currentX = x; currentX < x + length; currentX += 72) {
        svg += `<line x1="${currentX}" y1="${y}" x2="${currentX + 52}" y2="${y + 1}" class="draw-line draw-thin"/>`;
    }

    return svg;
}

function createShedSideView(x, topY, groundY, length) {
    let svg = `
        <rect x="${x}" y="${topY}" width="${length}" height="${groundY - topY}" class="draw-fill-none draw-line draw-heavy"/>
    `;

    for (let currentX = x + 12; currentX < x + length; currentX += 12) {
        svg += `<line x1="${currentX}" y1="${topY}" x2="${currentX}" y2="${groundY}" class="draw-line draw-normal"/>`;
    }

    return svg;
}

function createRafters(x, y, length, width) {
    let svg = "";
    const spacing = 55 * 1.05;

    for (let currentX = x + spacing; currentX < x + length; currentX += spacing) {
        svg += `<line x1="${currentX}" y1="${y}" x2="${currentX}" y2="${y + width}" class="draw-line draw-normal"/>`;
    }

    return svg;
}

function createBeams(x, y, length, width) {
    return `
        <line x1="${x}" y1="${y + 36}" x2="${x + length}" y2="${y + 36}" class="draw-line draw-heavy"/>
        <line x1="${x}" y1="${y + width - 36}" x2="${x + length}" y2="${y + width - 36}" class="draw-line draw-heavy"/>
    `;
}

function createTopPosts(x, y, length, width, shedX) {
    const positions = [
        x + 110,
        x + length * 0.54,
        Math.min(x + length - 42, shedX)
    ];

    return positions.map(position => `
        ${createPost(position, y + 36)}
        ${createPost(position, y + width - 36)}
    `).join("");
}

function createPost(x, y) {
    return `<rect x="${x - 7}" y="${y - 7}" width="14" height="14" class="draw-fill-white draw-line draw-heavy"/>`;
}

function createDiagonalBracing(x1, y1, x2, y2) {
    return `
        <line x1="${x1}" y1="${y1}" x2="${x2}" y2="${y2}" class="draw-line draw-dashed"/>
        <line x1="${x1}" y1="${y2}" x2="${x2}" y2="${y1}" class="draw-line draw-dashed"/>
        <line x1="${(x1 + x2) / 2 - 10}" y1="${(y1 + y2) / 2 - 10}" x2="${(x1 + x2) / 2 + 10}" y2="${(y1 + y2) / 2 + 10}" class="draw-line draw-heavy"/>
        <line x1="${(x1 + x2) / 2 + 10}" y1="${(y1 + y2) / 2 - 10}" x2="${(x1 + x2) / 2 - 10}" y2="${(y1 + y2) / 2 + 10}" class="draw-line draw-heavy"/>
    `;
}

function createShedTopView(x, y, length, width) {
    let svg = `
        <rect x="${x}" y="${y}" width="${length}" height="${width}" class="draw-fill-none draw-line draw-heavy"/>
        <line x1="${x + 8}" y1="${y + 8}" x2="${x + 8}" y2="${y + width - 8}" class="draw-line draw-dashed"/>
        <line x1="${x + length - 8}" y1="${y + 8}" x2="${x + length - 8}" y2="${y + width - 8}" class="draw-line draw-dashed"/>
        <line x1="${x + 8}" y1="${y + width - 8}" x2="${x + length - 8}" y2="${y + width - 8}" class="draw-line draw-dashed"/>
    `;

    for (let currentX = x + 12; currentX < x + length; currentX += 12) {
        svg += `<line x1="${currentX}" y1="${y}" x2="${currentX}" y2="${y + width}" class="draw-line draw-normal"/>`;
    }

    return svg;
}

function createRafterSpacingDimensions(x, y, length) {
    let svg = "";
    const spacing = 55 * 1.05;
    const count = Math.floor(length / spacing);

    for (let index = 0; index < count; index++) {
        const x1 = x + index * spacing;
        const x2 = Math.min(x + (index + 1) * spacing, x + length);
        svg += createHorizontalDimension(x1, y, x2, "0,55", 13);
    }

    return svg;
}

function createHorizontalDimension(x1, y, x2, label, fontSize = 22) {
    const midX = (x1 + x2) / 2;

    return `
        <line x1="${x1}" y1="${y}" x2="${x2}" y2="${y}" class="draw-line draw-dimension"/>
        <line x1="${x1}" y1="${y - 12}" x2="${x1}" y2="${y + 12}" class="draw-line draw-dimension"/>
        <line x1="${x2}" y1="${y - 12}" x2="${x2}" y2="${y + 12}" class="draw-line draw-dimension"/>
        <text x="${midX}" y="${y - 8}" class="draw-text" font-size="${fontSize}" text-anchor="middle">${label}</text>
    `;
}

function createVerticalDimension(x, y1, y2, label) {
    const midY = (y1 + y2) / 2;

    return `
        <line x1="${x}" y1="${y1}" x2="${x}" y2="${y2}" class="draw-line draw-dimension"/>
        <line x1="${x - 12}" y1="${y1}" x2="${x + 12}" y2="${y1}" class="draw-line draw-dimension"/>
        <line x1="${x - 12}" y1="${y2}" x2="${x + 12}" y2="${y2}" class="draw-line draw-dimension"/>
        <text x="${x - 12}" y="${midY}" class="draw-text" font-size="22" text-anchor="middle" transform="rotate(-90 ${x - 12} ${midY})">${label}</text>
    `;
}

function svgDefs() {
    return `
        <style>
            .draw-line { fill: none; stroke: #111111; stroke-linecap: square; stroke-linejoin: miter; }
            .draw-thin { stroke-width: 1; }
            .draw-normal { stroke-width: 1.4; }
            .draw-heavy { stroke-width: 2.4; }
            .draw-roof { stroke-width: 4; }
            .draw-light { stroke: #777777; stroke-width: 2; }
            .draw-dashed { stroke: #666666; stroke-width: 2; stroke-dasharray: 10 7; }
            .draw-dimension { stroke-width: 1.8; }
            .draw-fill-none { fill: none; }
            .draw-fill-white { fill: #ffffff; }
            .draw-text { fill: #111111; font-family: Arial, Helvetica, sans-serif; }
        </style>
    `;
}

function formatMeasure(valueInCm) {
    return (valueInCm / 100).toFixed(2).replace(".", ",");
}

document.querySelectorAll("input, select").forEach(element => {
    element.addEventListener("input", renderCarportDrawing);
    element.addEventListener("change", renderCarportDrawing);
});

renderCarportDrawing();
