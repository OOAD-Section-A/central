# Central
This repository will serve as a central source of information for everyone and potentially be used later for full integration.

## Problem Statement
Supply Chain Management Software

## Basic Info
- Everyone uses MVC patterns for all subsystems
- Everyone has to integrate with the error handling subsystems
- Your subsystem is considered complete when:
	- It is fully functional
	- It integrates successfully with at least two other subsystems
- Subsystems assigned at random

## Repo Structure
Every team creates a repository for their subsystem with the following structure:
```
/src
  /controllers
  /services
  /models
/tests
/docs
```
It would also be useful to set a ruleset to protect your main branch, because all members of the organisation have push access by default. Reference for this: https://github.com/orgs/community/discussions/172064

## Branching Strategy
```
main        → always stable
feature/*  → active work
```

## Docs
Please keep your READMEs updated with dependencies and how your subsystem exposes itself for integration (API design)!
