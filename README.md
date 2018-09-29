# Overview

This project provides a Last-Writer-Wins (LWW) Element Set implementation in class **LWW**. It is a conflict-free replicated data type (CRDT) data structure. You can simply initialize a LWW object and try out the following methods:

* **Add** function: Add an element to set
* **Remove** function: Remove an element from set
* **Exists** function: Check if an element exists in set
* **Get** function: Get a list of all elements in set

There are two implementations, one with Java HashMap class, another one with Redis score sorted set.

# Builds

To execute a local build, run `gradlew build` from the root repository directory.

# How to use Redis based LWW set

The Redis based LWW set is implemented in a separate class LWW_Redis, which uses score sorted set class provided by a Redis client [redisson](https://github.com/redisson/redisson). To try it out, you can specify Redis server configuration in _config\RedisConfig.json_ file.