const fs = require('fs');
const path = require('path');
const glob = require('glob');
const terser = require('terser');

const jsFilesPattern = 'src/main/resources/static/js/**/*.js';
const outputDir = path.join(__dirname, 'src/main/resources/static/js/minified');

if (!fs.existsSync(outputDir)) {
    fs.mkdirSync(outputDir, {recursive: true});
}

const jsFiles = glob.sync(jsFilesPattern);

jsFiles.forEach(jsFile => {
    const content = fs.readFileSync(jsFile, 'utf-8');
    console.log(`Original content of ${jsFile}:`, content);

    try {
        terser.minify(content, {
            mangle: {
                keep_classnames: false,
                keep_fnames: false
            },
            compress: {
                drop_console: true,
                drop_debugger: true
            },
            toplevel: true
        }).then(minified => {
            const outputFilePath = path.join(outputDir, path.basename(jsFile));
            if (minified.code && minified.code.trim().length > 0) {
                fs.writeFileSync(outputFilePath, minified.code);
                console.log(`Minified ${jsFile} to ${outputFilePath}`);
            } else {
                console.error(`Error: Minified content of ${jsFile} is empty. Writing original content.`);
                fs.writeFileSync(outputFilePath, content);
            }
        }).catch(err => {
            console.error(`Error minifying ${jsFile}:`, err);
            const outputFilePath = path.join(outputDir, path.basename(jsFile));
            fs.writeFileSync(outputFilePath, content);
        });
    } catch (err) {
        console.error(`Syntax error in ${jsFile}:`, err);
        const outputFilePath = path.join(outputDir, path.basename(jsFile));
        fs.writeFileSync(outputFilePath, content);
    }
});

const jsFilesDirectPattern = 'src/main/resources/static/js/*.js';
const jsFilesDirect = glob.sync(jsFilesDirectPattern);

jsFilesDirect.forEach(jsFile => {
    const content = fs.readFileSync(jsFile, 'utf-8');
    console.log(`Original content of ${jsFile}:`, content);

    try {
        terser.minify(content, {
            mangle: {
                keep_classnames: false,
                keep_fnames: false
            },
            compress: {
                drop_console: true,
                drop_debugger: true
            },
            toplevel: true
        }).then(minified => {
            const outputFilePath = path.join(outputDir, path.basename(jsFile));
            if (minified.code && minified.code.trim().length > 0) {
                fs.writeFileSync(outputFilePath, minified.code);
                console.log(`Minified ${jsFile} to ${outputFilePath}`);
            } else {
                console.error(`Error: Minified content of ${jsFile} is empty. Writing original content.`);
                fs.writeFileSync(outputFilePath, content);
            }
        }).catch(err => {
            console.error(`Error minifying ${jsFile}:`, err);
            const outputFilePath = path.join(outputDir, path.basename(jsFile));
            fs.writeFileSync(outputFilePath, content);
        });
    } catch (err) {
        console.error(`Syntax error in ${jsFile}:`, err);
        const outputFilePath = path.join(outputDir, path.basename(jsFile));
        fs.writeFileSync(outputFilePath, content);
    }
});