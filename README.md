# ClayJ
![Maven Central Version](https://img.shields.io/maven-central/v/io.github.christechs/ClayJ)


ClayJ is a high performance, zero dependency, UI layout library for Java. 

It is a pure Java port of [Clay](https://github.com/nicbarker/clay), designed to be framework and backend agnostic.

## Acknowledgments & Credits

This project would not exist without the work of the following developers:
* **[Nic Barker (nicbarker)](https://github.com/nicbarker)** - The creator of the original **[Clay](https://github.com/nicbarker/clay)** C library. ClayJ directly implements the layout mathematics and architectural philosophy designed by Nic.
* **[Patricio Whittingslow (soypat)](https://github.com/soypat)** - The author of **[Glay](https://github.com/soypat/glay)**, a Go port of Clay.

## Installation

ClayJ is available on **Maven Central**.

### Maven

```xml
<dependency>
    <groupId>io.github.christechs</groupId>
    <artifactId>ClayJ</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle (Groovy)

```groovy
implementation 'io.github.christechs:ClayJ:1.0.0'
```

### Gradle (Kotlin)

```kotlin
implementation("io.github.christechs:ClayJ:1.0.0")
```

## Quick Start

Since ClayJ is backend agnostic, you need to tell it how to measure text and how to handle errors. Everything else relies on a simple `beginLayout()` and `endLayout()` loop.

### Initialization and Setup

Call `ClayJ.initialize()` once at the start of your application. You must also provide a `MeasureTextFunction` so the engine knows how wide your strings are before rendering them.

```java
import io.github.christechs.clayj.math.Dimensions;
import static io.github.christechs.clayj.ClayJ.*;

// Initialize with max elements, max text cache, and window dimensions
initialize(8192, 8192, new Dimensions(1920, 1080));

// Set the Error Handler
setErrorHandler(errorType -> {
    System.err.println("ClayJ Error [" + errorType + "]: " + errorType.getDefaultMessage());
});

// Set the Text Measurement
setMeasureTextFunction((text, start, len, config, outDimensions) -> {
    float width = MyRenderer.measureText(text.subSequence(start, start + len), config.fontSize);
    float height = config.lineHeight > 0 ? config.lineHeight : config.fontSize;
    
    outDimensions.set(width, height);
});

```

### Layout Loop

In your application's render loop, you update the pointer/scroll state, declare your UI tree using the builder methods, and then iterate over the resulting render commands.

```java
import io.github.christechs.clayj.enums.*;
import io.github.christechs.clayj.LayoutResults;
import static io.github.christechs.clayj.ClayJ.*;

public void render(float deltaTime) {
    setLayoutDimensions(windowWidth, windowHeight);

    setPointerState(new Vector2(mouseX, mouseY), isMouseDown);
    updateScrollContainers(true, new Vector2(scrollDeltaX, scrollDeltaY), deltaTime);

    beginLayout();

    el(decl().id("Root").bg(15, 15, 18)
        .layout(layout().sizing(SizingType.GROW, 0, SizingType.GROW, 0)
                        .dir(LayoutDirection.TOP_TO_BOTTOM)
                        .padding(16).gap(8)), () -> {

        el(decl().id("Header").bg(38, 38, 45)
            .layout(layout().sizing(SizingType.GROW, 0, SizingType.FIXED, 50)
                            .align(LayoutAlignmentX.CENTER, LayoutAlignmentY.CENTER)), () -> {
            
            text("Welcome to ClayJ", txt().size(24).color(245, 245, 250));
        });

        boolean isHovered = pointerOver("MyButton");
        el(decl().id("MyButton").bg(isHovered ? 130 : 100, 140, 255)
            .layout(layout().sizing(SizingType.FIT, 0, SizingType.FIXED, 40)
                            .padding(16, 8)), () -> {
            
            text("Click Me!", txt().size(16).color(255, 255, 255));
        });

    });

    LayoutResults results = endLayout();

    renderUI(results);
}

```

### Rendering the Results

ClayJ outputs a flat array of `RenderCommand` objects. You loop through these and draw them whatever backend you prefer.

```java
private void renderUI(LayoutResults results) {
    for (int i = 0; i < results.length(); i++) {
        RenderCommand cmd = results.get(i);
        
        switch (cmd.commandType) {
            case RECTANGLE:
                MyRenderer.drawRect(cmd.boundingBox, cmd.renderData.backgroundColor, cmd.renderData.cornerRadius);
                break;
            case TEXT:
                MyRenderer.drawText(cmd.renderData.text, cmd.boundingBox.x, cmd.boundingBox.y, cmd.renderData.textColor);
                break;
            case SCISSOR_START:
                MyRenderer.pushClip(cmd.boundingBox);
                break;
            case SCISSOR_END:
                MyRenderer.popClip();
                break;
            // Handle IMAGE, BORDER, and CUSTOM
        }
    }
}

```

---

## License

ClayJ is licensed under the **zlib/libpng license**.

```text
Copyright (c) 2024 Christian Steenkamp

This software is provided 'as-is', without any express or implied warranty.
In no event will the authors be held liable for any damages arising from the
use of this software.

Permission is granted to anyone to use this software for any purpose,
including commercial applications, and to alter it and redistribute it
freely, subject to the following restrictions:

    1. The origin of this software must not be misrepresented; you must not
    claim that you wrote the original software. If you use this software in a
    product, an acknowledgment in the product documentation would be
    appreciated but is not required.

    2. Altered source versions must be plainly marked as such, and must not
    be misrepresented as being the original software.

    3. This notice may not be removed or altered from any source
    distribution.

```

### Third-Party Licenses

**Clay** (Original C Library)

* Copyright (c) 2024 Nic Barker
* Licensed under the zlib/libpng license.

**Glay** (Go Port)

BSD 3-Clause License

Copyright (c) 2023, Patricio Whittingslow

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
