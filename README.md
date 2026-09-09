[README.md](https://github.com/user-attachments/files/31876204/README.md)
# Library Management System

A console-based Java application that models a small library: it manages books, magazines, and
members, supports borrowing/returning items, persists data to disk, logs activity, runs a
background reporting task, and can inspect a class's structure at runtime via Reflection.

> A full, exhaustive line-by-line breakdown of every class also exists in
> `PROJECT_DOCUMENTATION.md` in this repo. This README is the shorter, higher-level summary.

---

## 1. What the Application Does

The system keeps track of:
- **Books** and **Magazines** — both are kinds of lendable library items, each with a status
  (`AVAILABLE`, `BORROWED`, `RESERVED`).
- **Members** — people who can borrow and return books.
- **Borrowing/returning** — updates a book's status and records the loan on the member.
- **Persistence** — the current books and members lists are saved to disk (`books.dat`,
  `members.dat`) using Java object serialization, and reloaded automatically the next time the
  program starts.
- **Activity logging** — every add/borrow/return action is timestamped and appended to `app.log`.
- **A background report** — a separate thread prints a small statistics summary (total books,
  borrowed/available counts, magazines, members) without blocking the main program.
- **Reflection-based inspection** — the structure of the `Book` class (its fields, methods,
  superclass, and interfaces) is printed at runtime using `java.lang.reflect`, rather than being
  hardcoded as text.

**Current limitation:** the application does not yet have the interactive numbered menu
(`1. Add book`, `2. Remove book`, etc.) described in the original task brief. `Main.java`
currently runs one fixed demonstration sequence — populate sample data, inspect a class, borrow a
book, run the background report, save to disk, shut down — rather than looping on user input. The
underlying logic for most menu actions already exists in `LibraryService`; they just aren't wired
up to a `Scanner`-driven menu loop yet.

---

## 2. Project Architecture

```
src/
├── Main.java              → entry point; wires services together and runs the demo flow
├── enums/                 → BookCategory, BookStatus, MembershipType
├── interfaces/             → Persistable (marker), BookFilter (functional, with a default method)
├── exception/              → BookNotFoundException, MemberNotFoundException,
│                             BookAlreadyBorrowedException, InvalidInputException
├── model/                  → Person, Member, LibraryItem, Book, Magazine, BookMetadata
├── service/                → LibraryService, FileStorageService, LoggerService
├── thread/                 → ThreadManager, BackgroundReportTask
└── util/                   → ValidationUtil, ReflectionInspector
```

**Class relationships, at a glance:**
- `Person` (abstract) → `Member`
- `LibraryItem` (abstract) → `Book`, `Magazine`
- `Person` and `LibraryItem` both implement the `Persistable` marker interface, which extends
  `Serializable` — this is what makes `Book`, `Magazine`, and `Member` objects writable to disk.
- `Book` implements `Comparable<Book>` (natural ordering by title) and holds a `BookMetadata`
  (an immutable value object for publisher/edition/language).
- `LibraryService` is the central coordinator: it holds the lists of books, magazines, and
  members, and owns the borrow/return/find/filter logic.
- `FileStorageService` and `LoggerService` are used by `LibraryService` and `Main` for
  persistence and logging respectively.
- `ThreadManager` runs a `BackgroundReportTask` (a `Runnable`) that reads from `LibraryService`
  to print statistics asynchronously.

For the full field-by-field, method-by-method explanation of every class and how they connect,
see `PROJECT_DOCUMENTATION.md`.

---

## 3. How to Build It

From the project root (the folder containing `src/`):

```bash
javac -d out $(find src -name "*.java")
```

This compiles every `.java` file under `src/` into an `out/` directory, preserving the package
structure.

## 4. How to Run It

```bash
java -cp out Main
```

Running it will:
- Create/read `books.dat` and `members.dat` in the current working directory (binary,
  serialized data).
- Create/append to `app.log` in the current working directory (plain-text activity log).

---

## 5. Dependency Injection

Dependency injection is only partially present. `LibraryService` accepts an *optional*
constructor that lets its book/magazine/member lists be **injected from outside**:

```java
public LibraryService(List<Book> books, List<Magazine> magazines, List<Member> members)
```

This is used in `Main.java` after loading data from disk — the loaded `List<Book>` is handed to
the service rather than the service creating its own list and the caller populating it
separately.

However, `LibraryService` still creates its own `LoggerService` internally
(`this.logger = new LoggerService();`) rather than having one passed in, and `BackgroundReportTask`
receives its `LibraryService` reference through **constructor injection**
(`new BackgroundReportTask(libraryService)`), which is the clearest DI example in the project —
the task doesn't construct or look up the service itself; it's given one to work with.

---

## 6. Where Streams Are Used

Currently, the Stream API is used in exactly one place: `BackgroundReportTask.run()`, to count
how many books are currently borrowed:

```java
long borrowedCount = books.stream()
        .filter(b -> b.getStatus() == enums.BookStatus.BORROWED)
        .count();
```

`LibraryService.filterBooks(BookFilter filter)` performs a conceptually similar filtering
operation, but currently uses a plain `for` loop instead of a stream.

---

## 7. Where Threads Are Used

Background work is handled with an `ExecutorService` rather than raw `Thread` objects:

- `ThreadManager` wraps `Executors.newFixedThreadPool(2)` — a small, reusable pool capped at two
  concurrent tasks, chosen to avoid the overhead of creating a brand-new OS thread for every
  background job while still letting the main application thread stay fully responsive (submitting
  a task returns immediately; it doesn't block the caller).
- `BackgroundReportTask implements Runnable` is the unit of work submitted to that pool
  (`threadManager.runTask(new BackgroundReportTask(libraryService))` in `Main`).
- `ThreadManager.shutdown()` performs a graceful shutdown: it stops accepting new tasks, waits up
  to 3 seconds for in-flight tasks to finish, and force-stops anything still running after that —
  including correctly re-raising the thread's interrupt flag if the wait itself gets interrupted.

Note: the task currently runs **once**, when submitted — it is not yet scheduled to repeat (e.g.
every 30 seconds), which the original task brief suggests as an example. Adding that would mean
swapping `ExecutorService` for a `ScheduledExecutorService` and using
`scheduleAtFixedRate(...)`.

---

## 8. How Serialization Works

Persistence is implemented with classic Java object serialization:

- `Person`, `LibraryItem` (and therefore `Member`, `Book`, `Magazine`) all implement `Persistable`,
  a marker interface that extends `Serializable` — so every domain object that needs to be saved
  is automatically eligible for serialization just by being part of that hierarchy.
- `FileStorageService.saveData(String filePath, List<T> data)` opens an `ObjectOutputStream` over
  a `FileOutputStream` and calls `writeObject(data)` to write an entire list in one call.
- `FileStorageService.loadData(String filePath)` does the reverse with an `ObjectInputStream` and
  `readObject()`, casting the result back to `List<T>`. If the file doesn't exist yet (first run),
  it catches `FileNotFoundException` and returns an empty list instead of treating that as an
  error.
- Every serializable class defines a `serialVersionUID`, which is what lets the JVM verify that a
  `.dat` file was written by a compatible version of the class before attempting to deserialize it.
- `Book.sessionViewCount` is marked `transient` — it's a per-run view counter that would be
  meaningless (and misleading) if it were saved and reloaded across separate program runs, so it's
  deliberately excluded from serialization and simply resets to `0` on load.

---

## 9. How Modules Are Structured

The project is organized by **responsibility**, not by feature:

| Package | Responsibility |
|---|---|
| `enums` | Fixed sets of values used throughout the domain, instead of raw strings |
| `interfaces` | Cross-cutting contracts (`Persistable`, `BookFilter`) implemented by multiple, otherwise-unrelated classes |
| `exception` | Custom checked exceptions representing specific failure conditions |
| `model` | The domain objects themselves — the data and the behavior directly tied to that data |
| `service` | Business logic and I/O — coordinating model objects, persistence, and logging |
| `thread` | Background/concurrent work, kept separate from the core business logic it reads from |
| `util` | Stateless helper functionality (validation, reflection) not tied to any one domain object |

`Main.java` sits outside all of these, in the default package, and is the only class that knows
about (and wires together) every layer.

---

## 10. Java Concepts Demonstrated

- **Encapsulation** — private fields with public getters/setters across every model class;
  `LibraryItem.status` uses `protected` deliberately so only the class hierarchy can touch it
  directly.
- **Inheritance** — `Person → Member`, `LibraryItem → {Book, Magazine}`.
- **Polymorphism** — `LibraryItem.getItemType()` is overridden differently by `Book` and
  `Magazine`; code working with a `LibraryItem` reference gets the right behavior automatically.
- **Abstraction** — `Person` and `LibraryItem` are `abstract`; `LibraryItem` declares
  `getItemType()` as `abstract`, forcing every subclass to define it.
- **Enums** — `BookCategory`, `BookStatus`, `MembershipType`, used instead of raw strings.
- **Marker interface** — `Persistable` (extends `Serializable`, no methods of its own).
- **Functional interface + default method** — `BookFilter` has one abstract method (`matches`)
  and a default method (`and`) that combines two filters together.
- **Method overloading** — `Member` has three constructors with different parameter sets.
- **Access modifiers** — `private`, `protected`, and `public` are all used meaningfully
  (package-private/default visibility is not currently used anywhere in the project).
- **`Comparable`** — `Book implements Comparable<Book>`, defining natural ordering by title.
- **Streams** — used in `BackgroundReportTask` (`filter` + `count`).
- **Regular expressions** — `ValidationUtil` validates email, phone number, ISBN, rating, and
  positive integers using precompiled `Pattern`s.
- **Custom exceptions, `throw` vs `throws`** — `LibraryService` *declares* exceptions with
  `throws` and *raises* them with `throw`; `Main` is where they're actually caught.
- **Files** — `LoggerService` appends timestamped entries to `app.log`.
- **Serialization** — `Serializable`, `ObjectOutputStream`, `ObjectInputStream`,
  `serialVersionUID`, and a `transient` field, all used together for save/load.
- **Threads** — `ExecutorService`-based background task via `ThreadManager` and
  `BackgroundReportTask`.
- **Reflection** — `ReflectionInspector` prints a class's superclass, interfaces, fields, and
  methods using `java.lang.reflect`.
- **Immutable objects** — `BookMetadata` is `final`, has only `private final` fields, no setters,
  and is fully initialized through its constructor.


